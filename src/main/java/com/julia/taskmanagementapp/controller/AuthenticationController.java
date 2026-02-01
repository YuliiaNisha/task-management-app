package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.user.UserLoginRequestDto;
import com.julia.taskmanagementapp.dto.user.UserLoginResponseDto;
import com.julia.taskmanagementapp.dto.user.UserRegistrationRequestDto;
import com.julia.taskmanagementapp.dto.user.UserResponseDto;
import com.julia.taskmanagementapp.security.AuthenticationService;
import com.julia.taskmanagementapp.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication",
        description = "Endpoints for user authentication")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Register a new user",
            description = "Registers a new user with the provided details.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User registered successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid registration data"

                    )
            }
    )
    @PostMapping("/registration")
    public UserResponseDto registerUser(
            @RequestBody @Valid UserRegistrationRequestDto requestDto
    ) {
        return userService.registerUser(requestDto);
    }

    @Operation(
            summary = "User login",
            description = "Authenticates a user with the provided credentials.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User authenticated successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – invalid username or password"
                    )
            }
    )
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
