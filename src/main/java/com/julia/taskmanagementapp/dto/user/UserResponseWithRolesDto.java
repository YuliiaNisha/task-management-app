package com.julia.taskmanagementapp.dto.user;

import java.util.Set;

public record UserResponseWithRolesDto(
        Long id,
        String profileUsername,
        String email,
        String firstName,
        String lastName,
        Set<String> roles
) {
}
