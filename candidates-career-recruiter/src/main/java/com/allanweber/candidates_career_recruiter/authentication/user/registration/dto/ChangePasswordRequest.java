package com.allanweber.candidates_career_recruiter.authentication.user.registration.dto;

import com.allanweber.candidates_career_recruiter.authentication.user.registration.validation.PasswordPolicy;
import com.allanweber.candidates_career_recruiter.core.utilities.Trimmable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
public class ChangePasswordRequest implements Trimmable {

    @NotEmpty(message = "Requisição de troca de senha é inválida")
    private String hash;

    @NotEmpty(message = "Informe o email")
    @Email(message = "Email é inválido")
    @Size(max = 128, message = "Email é muito grande")
    private String email;

    @NotEmpty(message = "Informe a senha")
    @PasswordPolicy
    @Size(max = 128, min = 10, message = "Tamanho da senha é inválido")
    private String password;

    @NotEmpty(message = "Informe a confirmação da senha")
    @Size(max = 128, min = 10, message = "Tamanho da confirmação da senha é inválido")
    private String confirmPassword;
}
