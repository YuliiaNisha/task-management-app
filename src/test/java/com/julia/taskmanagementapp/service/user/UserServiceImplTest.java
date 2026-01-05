package com.julia.taskmanagementapp.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.julia.taskmanagementapp.dto.user.UpdateUserRolesRequestDto;
import com.julia.taskmanagementapp.dto.user.UserRegistrationRequestDto;
import com.julia.taskmanagementapp.dto.user.UserResponseDto;
import com.julia.taskmanagementapp.dto.user.UserResponseWithRolesDto;
import com.julia.taskmanagementapp.mapper.UserMapper;
import com.julia.taskmanagementapp.model.Role;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.RoleRepository;
import com.julia.taskmanagementapp.repository.UserRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;
    private UserRegistrationRequestDto requestDto;
    private User user;
    private User savedUser;
    private UpdateUserRolesRequestDto updateRequestDto;
    private UserResponseWithRolesDto responseWithRolesDto;

    @BeforeEach
    void setUp() {
        requestDto = new UserRegistrationRequestDto(
                "name", "1234", "1234",
                "email", "First", "Last"
        );

        user = new User();
        user.setRoles(new HashSet<>());

        savedUser = new User();

        updateRequestDto = new UpdateUserRolesRequestDto(Set.of("ROLE_ADMIN"));

        responseWithRolesDto = new UserResponseWithRolesDto(
                1L, "name", "email",
                "First", "Last",
                Set.of("role")
        );

    }

    @Test
    void registerUser_validRequest_returnsUserResponseDto() {
        UserResponseDto userResponseDto = new UserResponseDto(
                1L, "name", "email",
                "First", "Last"
        );
        when(userRepository.existsByEmail(requestDto.email()))
                .thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.password()))
                .thenReturn("hfdhsuj");
        when(roleRepository.findByRole(Role.RoleName.ROLE_USER))
                .thenReturn(Optional.of(new Role()));
        when(userRepository.save(user))
                .thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(userResponseDto);

        UserResponseDto actual = userService.registerUser(requestDto);

        assertEquals(userResponseDto, actual);
    }

    @Test
    void updateRole_validRequest_returnsUserResponseWithRolesDto() {
        when(userRepository.findById(1L)).thenReturn(
                Optional.of(user)
        );
        when(roleRepository.findByRoleIn(updateRequestDto.roles()))
                .thenReturn(Set.of(new Role()));
        when(userRepository.save(user))
                .thenReturn(savedUser);
        when(userMapper.toDtoWithRoles(savedUser))
                .thenReturn(responseWithRolesDto);

        UserResponseWithRolesDto actual = userService.updateRole(1L, updateRequestDto);

        assertEquals(responseWithRolesDto, actual);
    }

    @Test
    void getUserProfileInfo() {
    }

    @Test
    void updateProfileInfo() {
    }
}
