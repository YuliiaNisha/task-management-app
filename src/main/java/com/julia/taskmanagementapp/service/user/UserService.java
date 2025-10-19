package com.julia.taskmanagementapp.service.user;

import com.julia.taskmanagementapp.dto.user.UpdateProfileInfoRequestDto;
import com.julia.taskmanagementapp.dto.user.UpdateUserRolesRequestDto;
import com.julia.taskmanagementapp.dto.user.UserProfileInfoDto;
import com.julia.taskmanagementapp.dto.user.UserRegistrationRequestDto;
import com.julia.taskmanagementapp.dto.user.UserResponseDto;
import com.julia.taskmanagementapp.dto.user.UserResponseWithRolesDto;
import com.julia.taskmanagementapp.model.User;

public interface UserService {
    UserResponseDto registerUser(
            UserRegistrationRequestDto requestDto
    );

    UserResponseWithRolesDto updateRole(Long id, UpdateUserRolesRequestDto requestDto);

    UserProfileInfoDto getUserProfileInfo(User user);

    UserProfileInfoDto updateProfileInfo(
            UpdateProfileInfoRequestDto requestDto, User user
    );
}
