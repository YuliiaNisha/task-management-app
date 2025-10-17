package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.user.UserLoginRequestDto;
import com.julia.taskmanagementapp.dto.user.UserLoginResponseDto;
import com.julia.taskmanagementapp.dto.user.UserRegistrationRequestDto;
import com.julia.taskmanagementapp.dto.user.UserResponseDto;
import com.julia.taskmanagementapp.exception.RegistrationException;
import com.julia.taskmanagementapp.security.AuthenticationService;
import com.julia.taskmanagementapp.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    UserResponseDto registerUser(
            @RequestBody UserRegistrationRequestDto requestDto
    ) {
        return userService.registerUser(requestDto);
    }

    @PostMapping("/login")
    UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
