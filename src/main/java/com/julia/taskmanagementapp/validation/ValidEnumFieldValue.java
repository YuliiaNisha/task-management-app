package com.julia.taskmanagementapp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumFieldValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnumFieldValue {
    String message() default "{constraints.validfieldvalue}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String fieldToValidate();
    Class<? extends Enum<?>> enumClass();
}
