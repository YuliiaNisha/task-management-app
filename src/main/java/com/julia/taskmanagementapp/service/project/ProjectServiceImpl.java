package com.julia.taskmanagementapp.service.project;

import com.julia.taskmanagementapp.dto.project.CreateProjectRequestDto;
import com.julia.taskmanagementapp.dto.project.ProjectDto;
import com.julia.taskmanagementapp.dto.project.UpdateProjectRequestDto;
import com.julia.taskmanagementapp.exception.EntityAlreadyExistsException;
import com.julia.taskmanagementapp.mapper.ProjectMapper;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.ProjectRepository;
import com.julia.taskmanagementapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectPermissionService projectPermissionService;

    @Override
    public ProjectDto create(CreateProjectRequestDto requestDto, User user) {
        Project project = projectMapper.toModel(requestDto);
        project.setStatus(Project.Status.INITIATED);
        project.setCreator(user);

        Set<Long> collaboratorIds = requestDto.collaboratorIds();
        checkCollaborators(collaboratorIds, user.getId());
        project.getCollaborators().addAll(userRepository.findAllById(collaboratorIds));

        return projectMapper.toDto(projectRepository.save(project));
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
        checkCollaborators(requestDto.collaboratorIds(), userId);
        project.getCollaborators().addAll(userRepository.findAllById(collaboratorIds));

        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public void delete(Long id, Long userId) {
        Project project = projectPermissionService.getProjectByIdIfCreator(id, userId);
        projectRepository.delete(project);
    }

    private void checkCollaborators(Set<Long> collaboratorIds, Long userId) {
        if (collaboratorIds.contains(userId)) {
            throw new EntityAlreadyExistsException("Creator cannot be added as a collaborator.");
        }
    }
}
