package com.julia.taskmanagementapp.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(this::getErrorMessage)
                .toList();
        return new ResponseEntity<>(getBody(errors), headers,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex
    ) {
        return new ResponseEntity<>(getBody(List.of(ex.getMessage())),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EnumFieldValidatorException.class)
    protected ResponseEntity<Object> handleEnumFieldValidatorException(
            EnumFieldValidatorException ex
    ) {
        return new ResponseEntity<>(
                getBody(List.of(ex.getMessage())),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(FieldMatchValidationException.class)
    protected ResponseEntity<Object> handleFieldMatchValidationException(
            FieldMatchValidationException ex
    ) {
        return new ResponseEntity<>(
                getBody(List.of(ex.getMessage())),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(RegistrationException.class)
    protected ResponseEntity<Object> handleRegistrationException(
            RegistrationException ex
    ) {
        return new ResponseEntity<>(
                getBody(List.of(ex.getMessage())),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    protected ResponseEntity<Object> handleJwtAuthenticationException(
            JwtAuthenticationException ex
    ) {
        return new ResponseEntity<>(
                getBody(List.of(ex.getMessage())),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    protected ResponseEntity<Object> handleEntityAlreadyExistsException(
            EntityAlreadyExistsException ex
    ) {
        return new ResponseEntity<>(
                getBody(List.of(ex.getMessage())),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException ex
    ) {
        return new ResponseEntity<>(
                getBody(List.of(ex.getMessage())),
                HttpStatus.BAD_REQUEST
        );
    }

    private String getErrorMessage(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            return fieldError.getField() + ": " + error.getDefaultMessage();
        }
        return error.getDefaultMessage();
    }

    private Map<String, Object> getBody(List<String> message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("errors", message);
        return body;
    }
}
