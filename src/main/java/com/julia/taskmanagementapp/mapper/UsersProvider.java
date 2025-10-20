package com.julia.taskmanagementapp.mapper;

import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import jdk.jfr.Name;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersProvider {
    private final UserRepository userRepository;

    @Named("getUsersFromIds")
    public Set<User> getUsersFromIds(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(userRepository.findAllById(userIds));
    }

    @Named("getIdsFromUsers")
    public Set<Long> getIdsFromUsers(Set<User> users) {
        if (users == null || users.isEmpty()) {
            return new HashSet<>();
        }
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    }
}
