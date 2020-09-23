package com.allanweber.candidatescareer.domain.user.registration.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
public class RememberMeRequest {
    @NotBlank
    private String email;
}
