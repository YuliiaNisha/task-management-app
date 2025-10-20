package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.project.CreateProjectRequestDto;
import com.julia.taskmanagementapp.dto.project.ProjectDto;
import com.julia.taskmanagementapp.dto.project.UpdateProjectRequestDto;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.service.project.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(
            @RequestBody @Valid CreateProjectRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return projectService.create(requestDto, user);
    }

    @GetMapping
    public Page<ProjectDto> getUserProjects(
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        return projectService.getUserProjects(user.getId(), pageable);
    }

    @GetMapping("/{id}")
    public ProjectDto getProjectById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return projectService.getProjectById(id, user.getId());
    }

    @PutMapping("/{id}")
    public ProjectDto update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProjectRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return projectService.update(id,requestDto, user.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        projectService.delete(id, user.getId());
    }
}
