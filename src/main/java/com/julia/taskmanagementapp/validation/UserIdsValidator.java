package com.julia.taskmanagementapp.validation;

import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserIdsValidator
        implements ConstraintValidator<UserIdsExistInDb, Collection<Long>> {
    private final UserRepository userRepository;

    @Override
    public boolean isValid(Collection<Long> ids, ConstraintValidatorContext context) {
        if (ids == null || ids.isEmpty()) {
            return true;
        }
        Set<Long> existingIds = userRepository.findAllById(ids)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        if (!existingIds.containsAll(ids)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Some of the provided values do not exist in DB"
                    )
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
