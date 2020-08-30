package com.allanweber.candidatescareer.domain.user.registration.dto;

import com.allanweber.candidatescareer.domain.user.registration.validation.PasswordConfirmed;
import com.allanweber.candidatescareer.domain.user.registration.validation.PasswordPolicy;
import com.allanweber.candidatescareer.domain.user.registration.validation.UniqueEmail;
import com.allanweber.candidatescareer.domain.user.registration.validation.UniqueUsername;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@Getter
@PasswordConfirmed
public class UserRegistration {

    @NotEmpty(message = "Enter your First Name")
    private final String firstName;

    @NotEmpty(message = "Enter your Last Name")
    private final String lastName;

    @NotEmpty(message = "Enter your User Name")
    @UniqueUsername
    private final String userName;

    @NotEmpty(message = "Enter your Email")
    @Email(message = "Email is not valid")
    @UniqueEmail
    private final String email;

    @NotEmpty(message = "Enter your Password")
    @PasswordPolicy
    private final String password;

    @NotEmpty(message = "Confirm your Password")
    private final String confirmPassword;
}

