package com.julia.taskmanagementapp.dto;

import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.validation.ValidEnumFieldValue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@ValidEnumFieldValue(
        fieldToValidate = "priority",
        enumClass = Task.Priority.class,
        message = "Invalid priority value. "
)
public record CreateTaskRequestDto(
        @NotBlank
        String name,
        @NotBlank
        @Size(max = 1000, message = "Description must be less than 1000 characters")
        String description,
        @NotBlank
        String priority,
        @NotNull(message = "Due date is required")
        @FutureOrPresent(message = "Due date cannot be in the past")
        LocalDate dueDate,
        @NotNull
        Long projectId,
        @NotNull
        Long assigneeId
) {
}
