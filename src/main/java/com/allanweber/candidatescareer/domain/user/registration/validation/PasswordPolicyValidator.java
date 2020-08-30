package com.allanweber.candidatescareer.domain.user.registration.validation;

import lombok.RequiredArgsConstructor;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class PasswordPolicyValidator implements ConstraintValidator<PasswordPolicy, String> {
    private final PasswordValidator passwordValidator;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        RuleResult ruleResult = passwordValidator.validate(new PasswordData(password));
        if(!ruleResult.isValid()) {
            StringBuilder messages = new StringBuilder();
            for (String message : passwordValidator.getMessages(ruleResult)) {
                messages.append(message).append(System.lineSeparator());
            }
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(messages.toString()).addConstraintViolation();
        }

        return ruleResult.isValid();
    }

}
