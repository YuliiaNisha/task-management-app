package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.user.UpdateProfileInfoRequestDto;
import com.julia.taskmanagementapp.dto.user.UpdateUserRolesRequestDto;
import com.julia.taskmanagementapp.dto.user.UserProfileInfoDto;
import com.julia.taskmanagementapp.dto.user.UserResponseWithRolesDto;
import com.julia.taskmanagementapp.exception.RegistrationException;
import com.julia.taskmanagementapp.model.Role;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.service.user.UserService;
import jakarta.validation.Valid;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/{id}/role")
    UserResponseWithRolesDto updateRole(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRolesRequestDto requestDto
    ) {
        return userService.updateRole(id, requestDto);
    }

    @GetMapping("/me")
    UserProfileInfoDto getUserProfileInfo(@AuthenticationPrincipal User authenticatedUser) {
        return userService.getUserProfileInfo(authenticatedUser);
    }

    @PatchMapping("/me")
    UserProfileInfoDto updateProfileInfo(
            @RequestBody @Valid UpdateProfileInfoRequestDto requestDto,
            @AuthenticationPrincipal User authenticatedUser) {
        return userService.updateProfileInfo(requestDto, authenticatedUser);
    }
}
