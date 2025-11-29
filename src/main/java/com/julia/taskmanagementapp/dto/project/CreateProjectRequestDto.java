package com.julia.taskmanagementapp.dto.project;

import com.julia.taskmanagementapp.validation.UserIdsExistInDb;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;

public record CreateProjectRequestDto(
        @NotBlank(message = "Project name is required")
        String name,
        @Size(max = 1000, message = "Description must be less than 1000 characters")
        String description,
        @NotNull(message = "Start date is required")
        LocalDate startDate,
        @NotNull(message = "End date is required")
        @FutureOrPresent(message = "End date cannot be in the past")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,
        @UserIdsExistInDb
        Set<Long> collaboratorIds
) {
}
