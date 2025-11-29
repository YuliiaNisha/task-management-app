package com.julia.taskmanagementapp.service.project;

import com.julia.taskmanagementapp.dto.project.CreateProjectRequestDto;
import com.julia.taskmanagementapp.dto.project.ProjectDto;
import com.julia.taskmanagementapp.dto.project.ProjectSearchParameters;
import com.julia.taskmanagementapp.dto.project.ProjectSearchParametersWithUserId;
import com.julia.taskmanagementapp.dto.project.UpdateProjectRequestDto;
import com.julia.taskmanagementapp.event.project.factory.ProjectEventFactory;
import com.julia.taskmanagementapp.event.project.factory.ProjectEventType;
import com.julia.taskmanagementapp.exception.EntityAlreadyExistsException;
import com.julia.taskmanagementapp.mapper.ProjectMapper;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.ProjectRepository;
import com.julia.taskmanagementapp.repository.SpecificationBuilder;
import com.julia.taskmanagementapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectPermissionService projectPermissionService;
    private final ApplicationEventPublisher publisher;
    private final ProjectEventFactory projectEventFactory;
    private final SpecificationBuilder<
            Project, ProjectSearchParametersWithUserId
            > specificationBuilder;

    @Override
    public ProjectDto create(CreateProjectRequestDto requestDto, User user) {
        Project project = projectMapper.toModel(requestDto);
        project.setStatus(Project.Status.INITIATED);
        project.setCreator(user);

        Set<Long> collaboratorIds = requestDto.collaboratorIds();
        checkCollaborators(collaboratorIds, user.getId());
        List<User> collaborators = userRepository.findAllById(collaboratorIds);
        project.getCollaborators().addAll(collaborators);

        Project savedProject = projectRepository.save(project);

        notifyUsers(ProjectEventType.PROJECT_CREATED, savedProject);

        return projectMapper.toDto(savedProject);
    }

    @Override
    public Page<ProjectDto> getUserProjects(Long userId, Pageable pageable) {
        return projectPermissionService.getProjectsIfCreatorOrCollaborator(
                userId, pageable
                )
                .map(projectMapper::toDto);
    }

    @Override
    public ProjectDto getProjectById(Long projectId, Long userId) {
        Project project = projectPermissionService.getProjectByIdIfCreatorOrCollaborator(
                projectId, userId
        );
        return projectMapper.toDto(project);
    }

    @Transactional
    @Override
    public ProjectDto update(Long id, UpdateProjectRequestDto requestDto, Long userId) {
        Project project = projectPermissionService.getProjectByIdIfCreator(id, userId);
        projectMapper.update(project, requestDto);

        Set<Long> collaboratorIds = requestDto.collaboratorIds();
        if (collaboratorIds != null) {
            checkCollaborators(requestDto.collaboratorIds(), userId);
            List<User> collaborators = userRepository.findAllById(collaboratorIds);
            project.getCollaborators().clear();
            project.getCollaborators().addAll(collaborators);
        }

        Project savedProject = projectRepository.save(project);

        notifyUsers(ProjectEventType.PROJECT_UPDATED, savedProject);

        return projectMapper.toDto(savedProject);
    }

    @Override
    public void delete(Long id, Long userId) {
        Project project = projectPermissionService.getProjectByIdIfCreator(id, userId);
        projectRepository.delete(project);

        notifyUsers(ProjectEventType.PROJECT_DELETED, project);
    }

    @Override
    public Page<ProjectDto> search(
            ProjectSearchParameters searchParameters, Pageable pageable, Long userId
    ) {
        ProjectSearchParametersWithUserId paramsWithUserId =
                projectMapper.toParamsWithUserId(searchParameters);
        paramsWithUserId.setUserId(userId);

        Specification<Project> specification = specificationBuilder.build(paramsWithUserId);

        return projectRepository.findAll(specification, pageable)
                .map(projectMapper::toDto);
    }

    private void checkCollaborators(Set<Long> collaboratorIds, Long userId) {
        if (collaboratorIds.contains(userId)) {
            throw new EntityAlreadyExistsException("Creator cannot be added as a collaborator.");
        }
    }

    private void notifyUsers(ProjectEventType type, Project project) {
        publisher.publishEvent(projectEventFactory.create(type, project));
    }
}
