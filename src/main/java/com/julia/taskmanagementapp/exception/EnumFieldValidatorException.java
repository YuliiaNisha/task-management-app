package com.julia.taskmanagementapp.exception;

public class EnumFieldValidatorException extends RuntimeException {
    public EnumFieldValidatorException(String message) {
        super(message);
    }

    public EnumFieldValidatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
