package com.julia.taskmanagementapp.dto.task;

import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.validation.enumfield.ValidEnumFieldValue;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@ValidEnumFieldValue(
        fieldToValidate = "status",
        enumClass = Task.Status.class, message = "Invalid status value. "
)
@ValidEnumFieldValue(fieldToValidate = "priority",
        enumClass = Task.Priority.class, message = "Invalid priority value. "
)
public record TaskSearchParameters(
        String name,


        String priority,


        String status,

        Long projectId,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dueDate
) {
}
