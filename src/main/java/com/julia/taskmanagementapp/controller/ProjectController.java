package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.project.CreateProjectRequestDto;
import com.julia.taskmanagementapp.dto.project.ProjectDto;
import com.julia.taskmanagementapp.dto.project.ProjectSearchParameters;
import com.julia.taskmanagementapp.dto.project.UpdateProjectRequestDto;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Projects",
        description = "Endpoints for managing projects")
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @Operation(
            summary = "Create a new project",
            description = "Creates a new project with the provided details. "
                    + "The authenticated user will be set as the creator of the project.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Project created successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    )
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(
            @RequestBody @Valid CreateProjectRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return projectService.create(requestDto, user);
    }

    @Operation(
            summary = "Get projects for the authenticated user",
            description = "Returns a paginated list of projects "
            + "where the currently authenticated user "
                    + "is either the creator or a collaborator.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Projects retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    )
            }
    )
    @GetMapping
    public Page<ProjectDto> getUserProjects(
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        return projectService.getUserProjects(user.getId(), pageable);
    }

    @Operation(
            summary = "Get project by ID",
            description = "Returns detailed information about a project. "
                    + "A project can be viewed only if the currently "
            + "authenticated user is the creator "
                    + "or a collaborator of the project.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – only the project’s creator "
                            + "or collaborators may view this project"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Project not found"
                    )
            }
    )
    @GetMapping("/{id}")
    public ProjectDto getProjectById(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @AuthenticationPrincipal User user
    ) {
        return projectService.getProjectById(id, user.getId());
    }

    @Operation(
            summary = "Update a project",
            description = "Updates an existing project. Only the creator "
            + "of the project is permitted to modify it.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project updated successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – only the project’s creator "
                            + "may update this project"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Project not found"
                    )
            }
    )
    @PutMapping("/{id}")
    public ProjectDto update(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @RequestBody @Valid UpdateProjectRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return projectService.update(id,requestDto, user.getId());
    }

    @Operation(
            summary = "Delete a project",
            description = "Deletes a project. Only the creator "
            + "of the project is permitted to delete it.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Project deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – only the project’s creator "
                            + "may delete this project"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Project not found"
                    )
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @AuthenticationPrincipal User user
    ) {
        projectService.delete(id, user.getId());
    }

    @Operation(
            summary = "Search projects",
            description = "Searches for projects based on the provided criteria. "
                    + "Only projects where the currently authenticated user "
            + "is the creator or a collaborator will be returned.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Projects matching the search "
                            + "criteria retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid search parameters"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – user does not have permission "
                            + "to view projects outside their access"
                    )
            }
    )
    @GetMapping("/search")
    public Page<ProjectDto> search(
            @Valid @ModelAttribute ProjectSearchParameters searchParameters,
            Pageable pageable,
            @AuthenticationPrincipal User user
    ) {
        return projectService.search(searchParameters, pageable, user.getId());
    }
}
