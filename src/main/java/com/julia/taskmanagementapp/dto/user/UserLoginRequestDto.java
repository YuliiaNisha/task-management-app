package com.julia.taskmanagementapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @Email(message = "Invalid format of email")
        @NotBlank(message = "Email is required. Please provide your email.")
        String email,

        @NotBlank(message = "Password is required. Please provide your password.")
        @Size(min = 8, max = 25, message = "Password must be between {min} and {max} digits")
        String password
) {
}
