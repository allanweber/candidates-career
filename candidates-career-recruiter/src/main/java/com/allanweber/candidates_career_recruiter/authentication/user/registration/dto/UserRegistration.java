package com.allanweber.candidates_career_recruiter.authentication.user.registration.dto;

import com.allanweber.candidates_career_recruiter.authentication.user.registration.validation.PasswordConfirmed;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.validation.PasswordPolicy;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.validation.UniqueEmail;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.validation.UniqueUsername;
import com.allanweber.candidates_career_recruiter.core.utilities.Trimmable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@PasswordConfirmed
public class UserRegistration implements Trimmable {

    @NotEmpty(message = "Informe seu primeiro nome")
    @Size(max = 128, min = 3, message = "Tamanho do primeiro nome é inválido")
    private final String firstName;

    @NotEmpty(message = "Informe seu sobrenome")
    @Size(max = 128, min = 3, message = "Tamanho do sobrenome é inválido")
    private final String lastName;

    @NotEmpty(message = "Informe o nome de usuário")
    @Size(max = 128, min = 5, message = "Tamanho do nome de usuário é inválido")
    @UniqueUsername
    private final String userName;

    @NotBlank(message = "Informe o email")
    @Email(message = "Email é inválido")
    @Size(max = 128, message = "Email é muito grande")
    @UniqueEmail
    private final String email;

    @NotEmpty(message = "Informe a senha")
    @PasswordPolicy
    @Size(max = 128, min = 10, message = "Tamanho da senha é inválido")
    private final String password;

    @NotEmpty(message = "Informe a confirmação da senha")
    @Size(max = 128, min = 10, message = "Tamanho da confirmação da senha é inválido")
    private final String confirmPassword;
}

