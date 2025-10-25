package com.julia.taskmanagementapp.dto.project;

import java.time.LocalDate;
import java.util.Set;

public record ProjectDto(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        Set<Long> collaboratorIds
) {
}
