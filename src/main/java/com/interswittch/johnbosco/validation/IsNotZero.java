package com.interswittch.johnbosco.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("unused")
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsNotZeroValidator.class)
public @interface IsNotZero {

    String message() default "Amount values cannot be zero";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

