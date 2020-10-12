package com.allanweber.candidatescareer.authentication.auth.dto;

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
