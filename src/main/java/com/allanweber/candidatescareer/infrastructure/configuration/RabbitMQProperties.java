package com.allanweber.candidatescareer.infrastructure.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.queue.candidate")
@Configuration
@Data
public class RabbitMQProperties {
    private String exchange;

    private String routingKey;

    private String candidateCodeQueue;
}
