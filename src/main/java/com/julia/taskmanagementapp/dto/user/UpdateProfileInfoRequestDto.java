package com.julia.taskmanagementapp.dto.user;

import com.julia.taskmanagementapp.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@FieldMatch(
        first = "password",
        second = "repeatPassword",
        message = "Password and repeat password must match")
public record UpdateProfileInfoRequestDto(
        String profileUsername,
        @Size(min = 8, max = 25, message = "Password must be between {min} and {max} digits")
        String password,
        @Size(min = 8, max = 25, message = "Password must be between {min} and {max} digits")
        String repeatPassword,
        @Email(message = "Invalid format of email.")
        String email,
        String firstName,
        String lastName
) {
}
