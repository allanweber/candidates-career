package com.allanweber.candidatescareer.infrastructure.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.host")
@Configuration
@Data
public class AppHostConfiguration {

    private String frontEnd;
}
