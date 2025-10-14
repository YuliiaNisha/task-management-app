package com.julia.taskmanagementapp.service.project;

import com.julia.taskmanagementapp.dto.project.CreateProjectRequestDto;
import com.julia.taskmanagementapp.dto.project.ProjectDto;
import com.julia.taskmanagementapp.dto.project.UpdateProjectRequestDto;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.mapper.ProjectMapper;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectDto create(CreateProjectRequestDto requestDto) {
        Project project = projectMapper.toModel(requestDto);
        project.setStatus(Project.Status.INITIATED);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public ProjectDto getProjectById(Long id) {
        Project project = findProject(id);
        return projectMapper.toDto(project);
    }

    @Override
    public ProjectDto update(Long id, UpdateProjectRequestDto requestDto) {
        Project project = findProject(id);
        projectMapper.update(project, requestDto);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public void delete(Long id) {
        projectExists(id);
        projectRepository.deleteById(id);
    }

    private void projectExists(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("There is no project by id: " + id);
        }
    }

    private Project findProject(Long id) {
        return projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no project by id: " + id)
        );
    }
}
