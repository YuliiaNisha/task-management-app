package com.julia.taskmanagementapp.dto.project;

import java.time.LocalDate;

public record ProjectDto(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String status
) {
}
