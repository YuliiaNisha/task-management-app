package com.julia.taskmanagementapp.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommentRequestDto(
        @NotNull(message = "Task id is required")
        Long taskId,
        @NotBlank(message = "Comment cannot be empty")
        String text
) {
}
