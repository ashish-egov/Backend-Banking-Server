package com.example.BackendBankingService.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidAccountIdValidator implements ConstraintValidator<ValidAccountId, Long> {

    @Override
    public boolean isValid(Long accountId, ConstraintValidatorContext context) {
        if (accountId == null) {
            return true;
        }
        String accountIdStr = String.valueOf(accountId);
        return accountIdStr.length() == 11 && accountIdStr.matches("\\d+");
    }
}
