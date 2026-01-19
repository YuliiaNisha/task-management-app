package com.julia.taskmanagementapp.validation.enumfield;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({
        ElementType.TYPE,
        ElementType.RECORD_COMPONENT,
        ElementType.PARAMETER,
        ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnumFieldValues {
    ValidEnumFieldValue[] value();
}
