package com.allanweber.candidatescareer.domain.user.registration.validation;

import com.allanweber.candidatescareer.domain.user.registration.dto.UserRegistration;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordConfirmedValidator implements ConstraintValidator<PasswordConfirmed, UserRegistration> {

    @Override
    public boolean isValid(UserRegistration user, ConstraintValidatorContext context) {
        String password = user.getPassword();
        String confirmedPassword = user.getConfirmPassword();
        return password.equals(confirmedPassword);
    }

}