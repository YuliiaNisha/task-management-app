package com.julia.taskmanagementapp.dto.label;

import jakarta.validation.constraints.Size;

public record UpdateLabelRequestDto(
        @Size(max = 50, message = "Label name must be less than 50 characters")
        String name,
        @Size(max = 25, message = "Label color name must be less than 25 characters")
        String color
) {
}
