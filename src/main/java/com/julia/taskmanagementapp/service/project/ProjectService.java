package com.julia.taskmanagementapp.service.project;

import com.julia.taskmanagementapp.dto.project.CreateProjectRequestDto;
import com.julia.taskmanagementapp.dto.project.ProjectDto;
import com.julia.taskmanagementapp.dto.project.UpdateProjectRequestDto;

public interface ProjectService {
    ProjectDto create(CreateProjectRequestDto requestDto);

    ProjectDto getProjectById(Long id);

    ProjectDto update(Long id, UpdateProjectRequestDto requestDto);

    void delete(Long id);
}
