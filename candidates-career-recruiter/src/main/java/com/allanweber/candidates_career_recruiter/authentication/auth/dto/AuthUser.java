package com.allanweber.candidates_career_recruiter.authentication.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthUser {

    private String userName;

    private String name;
}
