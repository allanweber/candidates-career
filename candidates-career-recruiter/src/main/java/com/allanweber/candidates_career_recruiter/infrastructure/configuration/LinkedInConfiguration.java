package com.allanweber.candidates_career_recruiter.infrastructure.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.linkedin")
@Configuration
@Data
public class LinkedInConfiguration {

    private String clientId;

    private String clientSecret;

    private String redirectHost;
}
