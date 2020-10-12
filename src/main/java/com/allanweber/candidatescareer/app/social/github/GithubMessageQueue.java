package com.allanweber.candidatescareer.app.social.github;

import com.allanweber.candidatescareer.app.social.dto.GitHubProfileMessage;
import com.allanweber.candidatescareer.infrastructure.configuration.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class GithubMessageQueue {

    private final AmqpTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitProperties;

    public void send(GitHubProfileMessage message) {
        rabbitTemplate.convertAndSend(rabbitProperties.getExchange(), rabbitProperties.getRoutingKey(), message);
        if (log.isInfoEnabled()) {
            log.info("Message sent Queue -> {} | Exchange -> {} | RoutingKey -> {}: {}",
                    rabbitProperties.getCandidateCodeQueue(), rabbitProperties.getExchange(), rabbitProperties.getRoutingKey(), message);
        }
    }
}
