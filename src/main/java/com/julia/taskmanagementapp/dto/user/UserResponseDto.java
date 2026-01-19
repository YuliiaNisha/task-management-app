package com.julia.taskmanagementapp.dto.user;

public record UserResponseDto(
        Long id,
        String profileUsername,
        String email,
        String firstName,
        String lastName
) {
}
