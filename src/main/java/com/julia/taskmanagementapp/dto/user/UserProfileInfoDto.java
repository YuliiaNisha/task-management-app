package com.julia.taskmanagementapp.dto.user;

import jakarta.persistence.Column;

public record UserProfileInfoDto(
        String profileUsername,
        String email,
        String firstName,
        String lastName
) {
}
