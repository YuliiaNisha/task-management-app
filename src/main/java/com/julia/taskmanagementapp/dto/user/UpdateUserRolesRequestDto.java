package com.julia.taskmanagementapp.dto.user;

import com.julia.taskmanagementapp.model.Role;
import com.julia.taskmanagementapp.validation.ValidEnumCollection;
import java.util.Set;

public record UpdateUserRolesRequestDto(
        @ValidEnumCollection(enumClass = Role.RoleName.class)
        Set<String> roles
) {
}
