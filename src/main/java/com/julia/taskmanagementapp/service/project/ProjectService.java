package com.julia.taskmanagementapp.service.project;

import com.julia.taskmanagementapp.dto.project.CreateProjectRequestDto;
import com.julia.taskmanagementapp.dto.project.ProjectDto;
import com.julia.taskmanagementapp.dto.project.ProjectSearchParameters;
import com.julia.taskmanagementapp.dto.project.UpdateProjectRequestDto;
import com.julia.taskmanagementapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectDto create(CreateProjectRequestDto requestDto, User user);

    Page<ProjectDto> getUserProjects(Long userId, Pageable pageable);

    ProjectDto getProjectById(Long projectId, Long userId);

    ProjectDto update(Long id, UpdateProjectRequestDto requestDto, Long userId);

    void delete(Long id, Long userId);

    Page<ProjectDto> search(
            ProjectSearchParameters searchParameters,
            Pageable pageable,
            Long userId
    );
}
