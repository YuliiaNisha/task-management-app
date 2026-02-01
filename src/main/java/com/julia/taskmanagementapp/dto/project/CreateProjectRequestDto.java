package com.julia.taskmanagementapp.dto.project;

import com.julia.taskmanagementapp.validation.UserIdsExistInDb;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

public record CreateProjectRequestDto(
        @Size(max = 255, message = "Project name must be less than {max} characters")
        @NotBlank(message = "Project name is required")
        String name,
        @Size(max = 255, message = "Description must be less than {max} characters")
        String description,
        @NotNull(message = "Start date is required")
        LocalDate startDate,
        @NotNull(message = "End date is required")
        @FutureOrPresent(message = "End date cannot be in the past")
        LocalDate endDate,
        @UserIdsExistInDb
        Set<@NotNull Long> collaboratorIds
) {
}
