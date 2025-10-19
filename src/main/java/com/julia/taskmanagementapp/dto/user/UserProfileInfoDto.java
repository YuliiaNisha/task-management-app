package com.julia.taskmanagementapp.dto.user;

public record UserProfileInfoDto(
        String profileUsername,
        String email,
        String firstName,
        String lastName
) {
}
