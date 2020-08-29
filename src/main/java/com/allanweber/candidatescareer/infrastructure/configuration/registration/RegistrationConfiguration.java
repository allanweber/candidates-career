package com.allanweber.candidatescareer.infrastructure.configuration.registration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "app.registration")
@Configuration
@Data
public class RegistrationConfiguration {

    @NotBlank
    private String verificationHost;

    private boolean emailVerificationEnabled;
}
