package com.julia.taskmanagementapp.security;

import com.julia.taskmanagementapp.dto.user.UserLoginRequestDto;
import com.julia.taskmanagementapp.dto.user.UserLoginResponseDto;

public interface AuthenticationService {
    UserLoginResponseDto authenticate(UserLoginRequestDto requestDto);
}
