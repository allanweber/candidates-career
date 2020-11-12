package com.allanweber.candidates_career_recruiter.authentication.auth.dto;

import com.allanweber.candidates_career_recruiter.authentication.user.registration.validation.PasswordPolicy;
import com.allanweber.candidates_career_recruiter.core.utilities.Trimmable;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Data
public class LoginRequest implements Trimmable {

    @NotBlank(message = "Informe o email")
    @Email(message = "Email é inválido")
    @Size(max = 128, message = "Email é muito grande")
    private String email;

    @NotBlank(message = "Informe a senha")
    @PasswordPolicy
    @Size(max = 128, min = 10, message = "Tamanho da senha é inválido")
    private String password;
}

