package com.julia.taskmanagementapp.validation;

import com.julia.taskmanagementapp.exception.FieldMatchValidationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String first;
    private String second;
    private String message;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.first = constraintAnnotation.first();
        this.second = constraintAnnotation.second();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
            Field firstField = getField(object, first);
            Field secondField = getField(object, second);
            isSameType(firstField, secondField);
            Object firstValue = firstField.get(object);
            Object secondValue = secondField.get(object);
            if (firstValue == null || !firstValue.equals(secondValue)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(second)
                        .addConstraintViolation();
                return false;
            }
            return true;
        } catch (IllegalAccessException e) {
            throw new FieldMatchValidationException("Can't validate fields: ", e);
        }
    }

    private Field getField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new FieldMatchValidationException("Can't get field: " + fieldName, e);
        }
    }

    private void isSameType(Field firstField, Field secondField) {
        if (!firstField.getType().equals(secondField.getType())) {
            throw new FieldMatchValidationException(
                    "Fields types do not match. First: "
                            + firstField.getType().getName()
                            + " second: " + secondField.getType().getName());
        }
    }
}
