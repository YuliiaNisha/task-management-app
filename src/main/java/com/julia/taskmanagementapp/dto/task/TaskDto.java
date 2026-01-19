package com.julia.taskmanagementapp.dto.task;

import java.time.LocalDate;
import java.util.Set;

public record TaskDto(
        Long id,
        String name,
        String description,
        String priority,
        String status,
        LocalDate dueDate,
        Long projectId,
        Long assigneeId,
        Set<Long> labelIds
) {
}
