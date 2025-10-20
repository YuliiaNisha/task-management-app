package com.julia.taskmanagementapp.dto.project;

import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.validation.UserIdsExistInDb;
import com.julia.taskmanagementapp.validation.ValidEnumFieldValue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@ValidEnumFieldValue(
        fieldToValidate = "status",
        enumClass = Project.Status.class,
        message = "Invalid status value. "
)
public record UpdateProjectRequestDto(
        String name,
        @Size(max = 1000, message = "Description must be less than 1000 characters")
        String description,
        LocalDate startDate,
        @FutureOrPresent(message = "End date cannot be in the past")
        LocalDate endDate,
        String status,
        @UserIdsExistInDb
        Set<Long> collaboratorIds
) {
}
