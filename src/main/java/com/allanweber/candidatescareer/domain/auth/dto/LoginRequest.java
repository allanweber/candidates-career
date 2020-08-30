package com.allanweber.candidatescareer.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Data
public class LoginRequest {

    @NotBlank
    private String user;

    @NotBlank
    private String password;
}

