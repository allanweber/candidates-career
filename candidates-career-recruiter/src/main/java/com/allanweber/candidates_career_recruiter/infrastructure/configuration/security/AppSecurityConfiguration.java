package com.allanweber.candidates_career_recruiter.infrastructure.configuration.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "app.security")
@Configuration
@Data
public class AppSecurityConfiguration {

    private boolean emailVerificationEnabled;

    @NotBlank
    private String jwtSecret;
}
