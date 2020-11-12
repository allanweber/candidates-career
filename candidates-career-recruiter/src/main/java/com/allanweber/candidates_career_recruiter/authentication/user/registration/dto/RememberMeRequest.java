package com.allanweber.candidates_career_recruiter.authentication.user.registration.dto;

import com.allanweber.candidates_career_recruiter.core.utilities.Trimmable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
public class RememberMeRequest implements Trimmable {
    @NotBlank
    @Email(message = "Email é inválido")
    @Size(max = 128, message = "Email é muito grande")
    private String email;
}
