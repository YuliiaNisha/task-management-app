package com.julia.taskmanagementapp.service.user;

import com.julia.taskmanagementapp.dto.user.UpdateProfileInfoRequestDto;
import com.julia.taskmanagementapp.dto.user.UserProfileInfoDto;
import com.julia.taskmanagementapp.dto.user.UserRegistrationRequestDto;
import com.julia.taskmanagementapp.dto.user.UserResponseDto;
import com.julia.taskmanagementapp.exception.RegistrationException;
import com.julia.taskmanagementapp.model.User;

public interface UserService {
    UserResponseDto registerUser(
            UserRegistrationRequestDto requestDto
    );

    UserProfileInfoDto getUserProfileInfo(User user);

    UserProfileInfoDto updateProfileInfo(
            UpdateProfileInfoRequestDto requestDto, User user
    );
}
