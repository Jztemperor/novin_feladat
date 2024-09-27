package com.md.backend.annotation;

import com.md.backend.validator.DueDateAfterIssueDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DueDateAfterIssueDateValidator.class)
public @interface DueDateAfterIssueDate {
    String message() default "Az esedékesség dátumának később kell lennie, mint a létrehozás dátuma.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
