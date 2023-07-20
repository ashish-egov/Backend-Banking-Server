package com.example.BackendBankingService.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidAccountIdValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAccountId {

    String message() default "Account ID must be null or a long of 11 digits";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}