package com.allanweber.candidatescareer.authentication.user.registration.validation;

import com.allanweber.candidatescareer.authentication.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return ! userService.emailExists(username);
    }

}
