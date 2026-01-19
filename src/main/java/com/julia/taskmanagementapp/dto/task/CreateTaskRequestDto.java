package com.julia.taskmanagementapp.dto.task;

import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.validation.enumfield.ValidEnumFieldValue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@ValidEnumFieldValue(
        fieldToValidate = "priority",
        enumClass = Task.Priority.class,
        message = "Invalid priority value. "
)
public record CreateTaskRequestDto(
        @NotBlank(message = "Task name is required")
        String name,
        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description must be less than 1000 characters")
        String description,
        @NotBlank(message = "Priority is required")
        String priority,
        @NotNull(message = "Due date is required")
        @FutureOrPresent(message = "Due date cannot be in the past")
        LocalDate dueDate,
        @NotNull(message = "Project id is required")
        Long projectId,
        @NotNull(message = "Assignee id is required")
        Long assigneeId,
        Set<Long> labelIds
) {
}
