package com.allanweber.candidatescareer.authentication.user.registration.validation;

import com.allanweber.candidatescareer.authentication.user.registration.dto.UserRegistration;
import lombok.NoArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@NoArgsConstructor
public class PasswordConfirmedValidator implements ConstraintValidator<PasswordConfirmed, UserRegistration> {

    @Override
    public boolean isValid(UserRegistration user, ConstraintValidatorContext context) {
        String password = user.getPassword();
        String confirmedPassword = user.getConfirmPassword();
        return password.equals(confirmedPassword);
    }

}