package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.comment.CommentDto;
import com.julia.taskmanagementapp.dto.comment.CreateCommentRequestDto;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comments",
        description = "Endpoints for managing comments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @Operation(
            summary = "Create a new comment",
            description = "Creates a new comment on a task. Only the creator of the project "
                    + "or a collaborator of the project associated with the task "
            + "is permitted to add a comment.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Comment created successfully"
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
    public CommentDto create(
            @RequestBody @Valid CreateCommentRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return commentService.create(requestDto, user);
    }

    @Operation(
            summary = "Get comments for a task",
            description = "Returns a paginated list of comments for the specified task. "
                    + "Only the creator of the project or collaborators of the project "
            + "associated with the task may view the comments.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comments retrieved successfully"
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
                            + "or collaborators may view comments for this task"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found"
                    )
            }
    )
    @GetMapping
    public Page<CommentDto> getCommentsByTaskId(
            @RequestParam Long taskId,
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        return commentService.getCommentsByTaskId(taskId,user.getId(), pageable);
    }
}
