package com.julia.taskmanagementapp.service.user;

import com.julia.taskmanagementapp.dto.user.UpdateProfileInfoRequestDto;
import com.julia.taskmanagementapp.dto.user.UpdateUserRolesRequestDto;
import com.julia.taskmanagementapp.dto.user.UserProfileInfoDto;
import com.julia.taskmanagementapp.dto.user.UserRegistrationRequestDto;
import com.julia.taskmanagementapp.dto.user.UserResponseDto;
import com.julia.taskmanagementapp.dto.user.UserResponseWithRolesDto;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.exception.RegistrationException;
import com.julia.taskmanagementapp.mapper.UserMapper;
import com.julia.taskmanagementapp.model.Role;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.RoleRepository;
import com.julia.taskmanagementapp.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserResponseDto registerUser(
            UserRegistrationRequestDto requestDto
    ) {
        checkUserAlreadyExists(requestDto.email());
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        user.getRoles().add(getRoleUser());
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    @Override
    public UserResponseWithRolesDto updateRole(Long id, UpdateUserRolesRequestDto requestDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no user by id: " + id)
        );
        Set<Role> roles = roleRepository.findByRoleIn(requestDto.roles());
        if (roles != null && !roles.isEmpty()) {
            user.setRoles(roles);
            return userMapper.toDtoWithRoles(userRepository.save(user));
        }
        return userMapper.toDtoWithRoles(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserProfileInfoDto getUserProfileInfo(User user) {
        return userMapper.toProfileInfo(user);
    }

    @Transactional
    @Override
    public UserProfileInfoDto updateProfileInfo(
            UpdateProfileInfoRequestDto requestDto,
            User user
    ) {
        if (requestDto.email() != null && !requestDto.email().equalsIgnoreCase(
                                user.getEmail()
                        )
        ) {
            checkUserAlreadyExists(requestDto.email());
        }
        userMapper.updateProfileInfo(user, requestDto);
        if (requestDto.password() != null) {
            user.setPassword(passwordEncoder.encode(requestDto.password()));
        }
        User savedUser = userRepository.save(user);
        return userMapper.toProfileInfo(savedUser);
    }

    private void checkUserAlreadyExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationException("Can't register user. User with email: "
                    + email + " is already registered.");
        }
    }

    private Role getRoleUser() {
        return roleRepository.findByRole(Role.RoleName.ROLE_USER)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "There is no role by name "
                                        + Role.RoleName.ROLE_USER.name()
                        )
                );
    }
}
