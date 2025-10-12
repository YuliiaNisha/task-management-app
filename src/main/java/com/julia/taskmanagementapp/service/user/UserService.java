package com.julia.taskmanagementapp.service.user;

import com.julia.taskmanagementapp.dto.user.UserRegistrationRequestDto;
import com.julia.taskmanagementapp.dto.user.UserResponseDto;
import com.julia.taskmanagementapp.exception.RegistrationException;

public interface UserService {
    UserResponseDto registerUser(
            UserRegistrationRequestDto requestDto
    ) throws RegistrationException;
}
