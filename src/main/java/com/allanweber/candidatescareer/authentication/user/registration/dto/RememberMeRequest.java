package com.allanweber.candidatescareer.authentication.user.registration.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
public class RememberMeRequest {
    @NotBlank
    private String email;
}
