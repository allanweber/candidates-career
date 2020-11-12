package com.allanweber.candidates_career_recruiter.authentication.auth.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class TokenDto {
    private String token;
    private long expirationIn;
    private Date issuedAt;
}
