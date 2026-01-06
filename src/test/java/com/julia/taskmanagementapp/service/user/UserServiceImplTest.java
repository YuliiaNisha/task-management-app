package com.julia.taskmanagementapp.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.julia.taskmanagementapp.dto.user.UpdateProfileInfoRequestDto;
import com.julia.taskmanagementapp.dto.user.UpdateUserRolesRequestDto;
import com.julia.taskmanagementapp.dto.user.UserProfileInfoDto;
import com.julia.taskmanagementapp.dto.user.UserRegistrationRequestDto;
import com.julia.taskmanagementapp.dto.user.UserResponseDto;
import com.julia.taskmanagementapp.dto.user.UserResponseWithRolesDto;
import com.julia.taskmanagementapp.exception.RegistrationException;
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
    private UserProfileInfoDto userProfileInfoDto;

    @BeforeEach
    void setUp() {
        requestDto = new UserRegistrationRequestDto(
                "name", "1234", "1234",
                "email", "First", "Last"
        );

        user = new User();
        user.setProfileUsername("profileName");
        user.setEmail("email");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setRoles(new HashSet<>());

        savedUser = new User();

        updateRequestDto = new UpdateUserRolesRequestDto(Set.of("ROLE_ADMIN"));

        responseWithRolesDto = new UserResponseWithRolesDto(
                1L, "name", "email",
                "First", "Last",
                Set.of("role")
        );

        userProfileInfoDto = new UserProfileInfoDto(
                "profileName", "email",
                "FirstName", "LastName"
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

        verify(userRepository).existsByEmail(requestDto.email());
        verify(passwordEncoder).encode(requestDto.password());
        verify(userRepository).save(user);
    }

    @Test
    void registerUser_userExists_returnsUserResponseDto() {
        UserResponseDto userResponseDto = new UserResponseDto(
                1L, "name", "email",
                "First", "Last"
        );
        when(userRepository.existsByEmail(requestDto.email()))
                .thenReturn(true);

        assertThrows(
                RegistrationException.class,
                () -> userService.registerUser(requestDto)
        );

        verify(userRepository).existsByEmail(requestDto.email());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper, passwordEncoder);
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
        verify(roleRepository).findByRoleIn(updateRequestDto.roles());
    }

    @Test
    void getUserProfileInfo_validRequest_returnDto() {
        when(userMapper.toProfileInfo(user))
                .thenReturn(userProfileInfoDto);

        UserProfileInfoDto actual = userService.getUserProfileInfo(user);

        assertEquals(userProfileInfoDto, actual);
    }

    @Test
    void updateProfileInfo_updateAllFields_returnDto() {
        UpdateProfileInfoRequestDto updateRequestDto =
                new UpdateProfileInfoRequestDto(
                        "new profileUserName",
                        "newpassword123",
                        "newpassword123",
                        "new email",
                        "new FirstName",
                        "new LastName"
                );

        UserProfileInfoDto updatedUserDto = new UserProfileInfoDto(
                "new profileUserName",
                "new email", "new FirstName",
                "new LastName"
        );

        final String oldPassword = user.getPassword();
        final String oldEmail = user.getEmail();
        final String oldProfileName = user.getProfileUsername();
        final String oldFirstName = user.getFirstName();
        final String oldLastName = user.getLastName();

        when(userRepository.existsByEmail(updateRequestDto.email()))
                .thenReturn(false);
        doAnswer(invocation -> {
                    User user = (User) invocation.getArguments()[0];
                    UpdateProfileInfoRequestDto updateRequest =
                            (UpdateProfileInfoRequestDto) invocation.getArguments()[1];

                    user.setEmail(updateRequest.email());
                    user.setProfileUsername(updateRequest.profileUsername());
                    user.setFirstName(updateRequest.firstName());
                    user.setLastName(updateRequest.lastName());

                    return null;
                }
        ).when(userMapper).updateProfileInfo(user, updateRequestDto);
        when(passwordEncoder.encode(updateRequestDto.password()))
                .thenReturn("encodedPassword");
        when(userRepository.save(user))
                .thenAnswer(invocation -> invocation.getArguments()[0]);
        when(userMapper.toProfileInfo(any()))
                .thenReturn(updatedUserDto);

        UserProfileInfoDto actual = userService.updateProfileInfo(updateRequestDto, user);

        assertEquals(updatedUserDto, actual);
        assertNotEquals(oldPassword, user.getPassword());
        assertNotEquals(oldEmail, user.getEmail());
        assertNotEquals(oldProfileName, user.getProfileUsername());
        assertNotEquals(oldFirstName, user.getFirstName());
        assertNotEquals(oldLastName, user.getLastName());
    }
}
