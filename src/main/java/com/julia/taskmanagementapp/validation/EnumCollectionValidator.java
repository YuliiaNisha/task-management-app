package com.julia.taskmanagementapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumCollectionValidator implements ConstraintValidator<ValidEnumCollection, Collection<String>> {
    private Set<String> allowedValues;

    @Override
    public void initialize(ValidEnumCollection constraintAnnotation) {
        allowedValues = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Collection<String> values, ConstraintValidatorContext context) {
        if (values == null) {
            buildCustomConstraintViolation(context, "Roles cannot be null");
            return false;
        }
        if (values.isEmpty()) {
            buildCustomConstraintViolation(context, "Roles cannot be empty");
            return false;
        }
        if (!allowedValues.containsAll(values)) {
            buildCustomConstraintViolation(
                    context,
                    "Roles must match ENUM values: " + allowedValues
            );
            return false;
        }
        return true;
    }

    private void buildCustomConstraintViolation(
            ConstraintValidatorContext context, String message
    ) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
