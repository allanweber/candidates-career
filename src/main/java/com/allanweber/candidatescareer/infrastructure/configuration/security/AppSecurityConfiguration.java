package com.allanweber.candidatescareer.infrastructure.configuration.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "app.security")
@Configuration
@Data
public class AppSecurityConfiguration {

    @NotBlank
    private String verificationHost;

    private boolean emailVerificationEnabled;

    @NotBlank
    private String jwtSecret;
}
