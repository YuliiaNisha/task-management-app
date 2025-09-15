package com.julia.taskmanagementapp.validation;

import com.julia.taskmanagementapp.exception.EnumFieldValidatorException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumFieldValidator implements ConstraintValidator<ValidEnumFieldValue, Object> {
    private String fieldToValidate;
    private Class<? extends Enum<?>> enumClass;
    private String message;

    @Override
    public void initialize(ValidEnumFieldValue constraintAnnotation) {
        this.fieldToValidate = constraintAnnotation.fieldToValidate();
        this.enumClass = constraintAnnotation.enumClass();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
            Field field = object.getClass().getDeclaredField(fieldToValidate);
            field.setAccessible(true);
            Object objectToValidate = field.get(object);
            if (objectToValidate == null) {
                return true;
            }

            validateFieldTypeIsString(field);
            String stringToValidate = objectToValidate.toString();

            if (isEnumValue(stringToValidate)) {
                return true;
            }

            return buildViolation(context);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new EnumFieldValidatorException(
                    "Can't validate field: " + fieldToValidate, e);
        }
    }

    private boolean isEnumValue(String stringToValidate) {
        Enum<?>[] enumValues = enumClass.getEnumConstants();
        for (Enum<?> value : enumValues) {
            if (value.name().equals(stringToValidate)) {
                return true;
            }
        }
        return false;
    }

    private boolean buildViolation(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(generateFullMessage())
                .addPropertyNode(fieldToValidate)
                .addConstraintViolation();
        return false;
    }

    private String generateFullMessage() {
        String allowedValues =
                Arrays.stream(enumClass.getEnumConstants())
                        .map(Enum::name)
                        .collect(Collectors.joining(", "));
        return message + "Allowed values: " + allowedValues;
    }

    private void validateFieldTypeIsString(Field field) {
        if (!field.getType().equals(String.class)) {
            throw new EnumFieldValidatorException(
                    "Can't validate field: " + fieldToValidate + ". "
                            + "Expected field type: " + String.class.getSimpleName() + ", "
                            + "but found: " + field.getType().getSimpleName());
        }
    }
}
