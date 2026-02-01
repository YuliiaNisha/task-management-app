package com.julia.taskmanagementapp.dto.user;

import com.julia.taskmanagementapp.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@FieldMatch(first = "password",
        second = "repeatPassword",
        message = "Password and repeat password must match")
public record UserRegistrationRequestDto(
        @Size(max = 255, message = "Username must be less than {max}")
        @NotBlank(message = "Username is required. Please provide username.")
        String profileUsername,
        @NotBlank(message = "Password is required. Please provide your password.")
        @Size(min = 8, max = 25, message = "Password must be between {min} and {max} digits")
        String password,
        @NotBlank(message = "Please, repeat your password.")
        @Size(min = 8, max = 25,
                message = "Repeat password must be between {min} and {max} digits")
        String repeatPassword,
        @Email(message = "Invalid format of email.")
        @NotBlank(message = "Email is required. Please provide your email.")
        String email,
        @Size(max = 255, message = "First name must be less than {max}")
        @NotBlank(message = "First name is required. Please provide your first name.")
        String firstName,
        @Size(max = 255, message = "Last name must be less than {max}")
        @NotBlank(message = "Last name is required. Please provide your last name.")
        String lastName
) {
}
