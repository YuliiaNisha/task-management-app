package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.user.UpdateProfileInfoRequestDto;
import com.julia.taskmanagementapp.dto.user.UpdateUserRolesRequestDto;
import com.julia.taskmanagementapp.dto.user.UserProfileInfoDto;
import com.julia.taskmanagementapp.dto.user.UserResponseWithRolesDto;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users",
        description = "Endpoints for managing users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Update a user’s role",
            description = "Modifies the user’s roles. Accessible only to administrators.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The user's role has been successfully updated"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied – administrator privileges required"
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    UserResponseWithRolesDto updateRole(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRolesRequestDto requestDto
    ) {
        return userService.updateRole(id, requestDto);
    }

    @Operation(
            summary = "Get authenticated user profile",
            description = "Returns profile information for the currently authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User profile retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    )
            }
    )
    @GetMapping("/me")
    UserProfileInfoDto getUserProfileInfo(@AuthenticationPrincipal User authenticatedUser) {
        return userService.getUserProfileInfo(authenticatedUser);
    }

    @Operation(
            summary = "Update authenticated user profile",
            description = "Updates profile information for the currently authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Profile information updated successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – user does not have permission "
                            + "to modify this profile"
                    )
            }
    )
    @PatchMapping("/me")
    UserProfileInfoDto updateProfileInfo(
            @RequestBody @Valid UpdateProfileInfoRequestDto requestDto,
            @AuthenticationPrincipal User authenticatedUser) {
        return userService.updateProfileInfo(requestDto, authenticatedUser);
    }
}
