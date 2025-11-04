package com.julia.taskmanagementapp.dto.task;

import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.validation.ValidEnumFieldValue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

public record UpdateTaskRequestDto(
         String name,
         @Size(max = 1000, message = "Description must be less than 1000 characters")
         String description,
         @ValidEnumFieldValue(
                 fieldToValidate = "priority",
                 enumClass = Task.Priority.class,
                 message = "Invalid priority value. "
         )
         String priority,
         @ValidEnumFieldValue(
                 fieldToValidate = "status",
                 enumClass = Task.Status.class,
                 message = "Invalid status value. "
         )
         String status,
         @FutureOrPresent(message = "Due date cannot be in the past")
         LocalDate dueDate,
         Long projectId,
         Long assigneeId,

         Set<Long> labelIds
) {
}
