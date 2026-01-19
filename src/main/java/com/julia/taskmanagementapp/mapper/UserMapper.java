package com.julia.taskmanagementapp.mapper;

import com.julia.taskmanagementapp.config.MapperConfig;
import com.julia.taskmanagementapp.dto.user.UpdateProfileInfoRequestDto;
import com.julia.taskmanagementapp.dto.user.UserProfileInfoDto;
import com.julia.taskmanagementapp.dto.user.UserRegistrationRequestDto;
import com.julia.taskmanagementapp.dto.user.UserResponseDto;
import com.julia.taskmanagementapp.dto.user.UserResponseWithRolesDto;
import com.julia.taskmanagementapp.model.Role;
import com.julia.taskmanagementapp.model.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);

    UserResponseDto toDto(User user);

    UserProfileInfoDto toProfileInfo(User user);

    @Mapping(source = "roles", target = "roles", qualifiedByName = "getRoleNames")
    UserResponseWithRolesDto toDtoWithRoles(User user);

    @Mapping(target = "password", ignore = true)
    void updateProfileInfo(@MappingTarget User user, UpdateProfileInfoRequestDto requestDto);

    @Named("getRoleNames")
    default Set<String> getRoleNames(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getRole().name())
                .collect(Collectors.toSet());
    }
}
