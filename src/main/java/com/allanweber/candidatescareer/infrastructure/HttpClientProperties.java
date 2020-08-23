package com.allanweber.candidatescareer.infrastructure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.rest")
@Configuration
@Data
public class HttpClientProperties {

    private int connectTimeoutInMs;

    private int readTimeoutInMs;
}