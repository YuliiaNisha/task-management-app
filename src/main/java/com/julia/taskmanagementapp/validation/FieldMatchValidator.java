package com.julia.taskmanagementapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String first;
    private String second;
    private String notValidMessage;
    private String errorWithValidationMessage;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.first = constraintAnnotation.first();
        this.second = constraintAnnotation.second();
        this.notValidMessage = constraintAnnotation.message();
        this.errorWithValidationMessage = constraintAnnotation.errorWithValidationMessage();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
            Field firstField = getField(object, first);
            Field secondField = getField(object, second);
            if (firstField == null || secondField == null) {
                return false;
            }
            if (!firstField.getType().equals(secondField.getType())) {
                return false;
            }
            Object firstValue = firstField.get(object);
            Object secondValue = secondField.get(object);
            if (firstValue == null && secondValue == null) {
                return true;
            }
            if (firstValue == null || !firstValue.equals(secondValue)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(notValidMessage)
                        .addPropertyNode(second)
                        .addConstraintViolation();
                return false;
            }
            return true;
        } catch (IllegalAccessException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorWithValidationMessage)
                    .addPropertyNode(second)
                    .addConstraintViolation();
            return false;
        }
    }

    private Field getField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
