package com.allanweber.candidates_career_recruiter.infrastructure.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfiguration {

    private final RabbitMQProperties rabbitProperties;

    @Bean
    public Declarables fanoutBindings() {
        Queue codeQueue = new Queue(rabbitProperties.getCandidateCodeQueue(), true);
        DirectExchange exchange = new DirectExchange(rabbitProperties.getExchange());

        return new Declarables(
                codeQueue,
                exchange,
                BindingBuilder.bind(codeQueue).to(exchange).with(rabbitProperties.getRoutingKey()));
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
