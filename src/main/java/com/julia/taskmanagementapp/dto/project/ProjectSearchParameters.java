package com.julia.taskmanagementapp.dto.project;

import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.validation.enumfield.ValidEnumFieldValue;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@ValidEnumFieldValue(
        fieldToValidate = "status",
        enumClass = Project.Status.class,
        message = "Invalid status value. "
)
public record ProjectSearchParameters(
        String name,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,

        String status
) {
}
