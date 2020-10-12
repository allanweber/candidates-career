package com.allanweber.candidatescareer.authentication.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {
    private String type;

    private String token;

    private List<String> roles;

    private Long expirationIn;

    private Date issuedAt;

    private AuthUser user;
}

