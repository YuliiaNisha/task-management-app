package com.julia.taskmanagementapp.service.project;

import com.julia.taskmanagementapp.dto.project.CreateProjectRequestDto;
import com.julia.taskmanagementapp.dto.project.ProjectDto;
import com.julia.taskmanagementapp.dto.project.UpdateProjectRequestDto;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.mapper.ProjectMapper;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.repository.ProjectRepository;
import com.julia.taskmanagementapp.repository.TaskRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectDto create(CreateProjectRequestDto requestDto, Long id) {
        Project project = projectMapper.toModel(requestDto);
        project.setStatus(Project.Status.INITIATED);
        project.setCreatedById(id);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public Page<ProjectDto> getUserProjects(Long userId, Pageable pageable) {
        List<Long> projectIds = taskRepository.findByAssigneeId(userId)
                .stream()
                .map(Task::getProjectId)
                .distinct()
                .toList();
        if (projectIds.isEmpty()) {
            return Page.empty(pageable);
        }
        return projectRepository.findByIdIn(projectIds, pageable)
                .map(projectMapper::toDto);
    }

    @Override
    public ProjectDto getProjectById(Long id) {
        Project project = findProjectById(id);
        return projectMapper.toDto(project);
    }

    @Override
    public ProjectDto update(Long id, UpdateProjectRequestDto requestDto) {
        Project project = findProjectById(id);
        projectMapper.update(project, requestDto);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public void delete(Long id) {
        Project project = findProjectById(id);
        projectRepository.delete(project);
    }

    private void projectExists(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("There is no project by id: " + id);
        }
    }

    private Project findProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no project by id: " + id)
        );
    }
}
