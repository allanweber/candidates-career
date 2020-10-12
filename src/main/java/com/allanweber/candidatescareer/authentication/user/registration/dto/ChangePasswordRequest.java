package com.allanweber.candidatescareer.authentication.user.registration.dto;

import com.allanweber.candidatescareer.authentication.user.registration.validation.PasswordPolicy;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Getter
public class ChangePasswordRequest {

    @NotEmpty(message = "Hash is invalid")
    private String hash;

    @NotEmpty(message = "Enter your Email")
    @Email(message = "Email is not valid")
    private String email;

    @NotEmpty(message = "Enter your Password")
    @PasswordPolicy
    private String password;

    @NotEmpty(message = "Confirm your Password")
    private String confirmPassword;
}
