package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.task.CreateTaskRequestDto;
import com.julia.taskmanagementapp.dto.task.TaskDto;
import com.julia.taskmanagementapp.dto.task.TaskSearchParameters;
import com.julia.taskmanagementapp.dto.task.UpdateTaskRequestDto;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.service.task.TaskService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tasks",
        description = "Endpoints for managing tasks")
@RequestMapping("/tasks")
@RestController
@RequiredArgsConstructor
class TaskController {
    private final TaskService taskService;

    @Operation(
            summary = "Create a new task",
            description = "Creates a new task using the provided data. "
            + "Only the project’s creator is permitted to create tasks "
                    + "associated with the project.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Task created successfully"
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
                            description = "Forbidden – user does not have permission"
                                    + "to perform this action"
                    )
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto create(
            @RequestBody @Valid CreateTaskRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return taskService.create(requestDto, user.getId());
    }

    @Operation(
            summary = "Get tasks for a project",
            description = "Returns a paginated list of tasks belonging to the specified project. "
                    + "Only the project’s creator or collaborators "
                    + "are allowed to view these tasks.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tasks retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – user does not have permission "
                            + "to view tasks for this project"
                    )
            }
    )
    @GetMapping
    public Page<TaskDto> getTasksForProject(
            @RequestParam @Positive(message = "ID must be positive") Long projectId,
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        return taskService.getTasksForProject(projectId, user.getId(), pageable);
    }

    @Operation(
            summary = "Get task by ID",
            description = "Returns detailed information about a task. "
                    + "A task can be viewed only if the currently "
                    + "authenticated user is the creator "
                    + "or a collaborator of the project to which the task belongs.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid task ID"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – only the project’s creator "
                            + "or collaborators may view this task"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found"
                    )
            }
    )
    @GetMapping("/{id}")
    public TaskDto getTaskById(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @AuthenticationPrincipal User user
    ) {
        return taskService.getTaskById(id, user.getId());
    }

    @Operation(
            summary = "Update a task",
            description = "Updates an existing task. "
            + "Only the creator of the project associated with this task "
                    + "is permitted to modify it.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task updated successfully"
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
                                    + "may update this task"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found"
                    )
            }
    )
    @PutMapping("/{id}")
    public TaskDto update(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @RequestBody @Valid UpdateTaskRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return taskService.update(id, requestDto, user.getId());
    }

    @Operation(
            summary = "Delete a task",
            description = "Deletes a task. Only the creator of the project "
                    + "associated with this task "
                    + "is permitted to delete it.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Task deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – only the project’s creator "
                                    + "may delete this task"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found"
                    )
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable @Positive(message = "ID must be positive") Long id,
            @AuthenticationPrincipal User user
    ) {
        taskService.delete(id, user.getId());
    }

    @Operation(
            summary = "Assign a label to a task",
            description = "Assigns a label to the specified task. Only the creator of the project "
                    + "associated with this task is permitted to assign labels.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Label assigned to task successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid task or label ID"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – only the project’s creator "
                            + "may assign labels to this task"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task or label not found"
                    )
            }
    )
    @PostMapping("/{taskId}/labels/{labelId}")
    public TaskDto assignLabelToTask(
            @PathVariable @Positive(message = "ID must be positive") Long taskId,
            @PathVariable @Positive(message = "ID must be positive") Long labelId,
            @AuthenticationPrincipal User user
    ) {
        return taskService.assignLabelToTask(taskId, labelId, user.getId());
    }

    @Operation(
            summary = "Remove a label from a task",
            description = "Removes a label from the specified task. "
                    + "Only the creator of the project "
                    + "associated with this task is permitted to remove labels.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Label removed from task successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid task or label ID"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – only the project’s creator "
                            + "may remove labels from this task"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task or label not found"
                    )
            }
    )
    @DeleteMapping("/{taskId}/labels/{labelId}")
    public TaskDto removeLabelFromTask(
            @PathVariable @Positive(message = "ID must be positive") Long taskId,
            @PathVariable @Positive(message = "ID must be positive") Long labelId,
            @AuthenticationPrincipal User user
    ) {
        return taskService.removeLabelFromTask(taskId,labelId, user.getId());
    }

    @Operation(
            summary = "Search tasks",
            description = "Searches for tasks based on the provided criteria. "
                    + "Only tasks belonging to projects where the current user "
                    + " is either the creator or a collaborator will be returned.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tasks matching the search criteria "
                            + "retrieved successfully"
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
                            + "to view tasks for the specified project(s)"
                    )
            }
    )
    @GetMapping("/search")
    public Page<TaskDto> search(
            @Valid @ModelAttribute TaskSearchParameters taskSearchParameters,
            Pageable pageable,
            @AuthenticationPrincipal User user
    ) {
        return taskService.search(taskSearchParameters, pageable, user.getId());
    }
}
