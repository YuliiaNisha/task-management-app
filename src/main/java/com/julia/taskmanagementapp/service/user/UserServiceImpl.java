package com.julia.taskmanagementapp.service.user;

import com.julia.taskmanagementapp.dto.user.UpdateProfileInfoRequestDto;
import com.julia.taskmanagementapp.dto.user.UserProfileInfoDto;
import com.julia.taskmanagementapp.dto.user.UserRegistrationRequestDto;
import com.julia.taskmanagementapp.dto.user.UserResponseDto;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.exception.RegistrationException;
import com.julia.taskmanagementapp.mapper.UserMapper;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserResponseDto registerUser(
            UserRegistrationRequestDto requestDto
    ) {
        checkUserAlreadyExists(requestDto.email());
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

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
}
