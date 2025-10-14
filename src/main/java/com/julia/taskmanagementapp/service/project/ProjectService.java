package com.julia.taskmanagementapp.service.project;

import com.julia.taskmanagementapp.dto.project.CreateProjectRequestDto;
import com.julia.taskmanagementapp.dto.project.ProjectDto;
import com.julia.taskmanagementapp.dto.project.UpdateProjectRequestDto;
import com.julia.taskmanagementapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface ProjectService {
    ProjectDto create(CreateProjectRequestDto requestDto);

    Page<ProjectDto> getUserProjects(Long userId, Pageable pageable);

    ProjectDto getProjectById(Long id);

    ProjectDto update(Long id, UpdateProjectRequestDto requestDto);

    void delete(Long id);
}
