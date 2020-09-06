package com.allanweber.candidatescareer.infrastructure.configuration;

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

//    @Bean
//    FanoutExchange exchange() {
//        return new FanoutExchange(rabbitProperties.getExchange());
//    }
//
//    @Bean
//    Queue codeQueue() {
//        return new Queue(rabbitProperties.getCandidateCodeQueue(), true);
//    }


//    @Bean
//    public Binding binding() {
//        return BindingBuilder.bind(new Queue(rabbitProperties.getCandidateCodeQueue(), true))
//                .to(new DirectExchange(rabbitProperties.getExchange()))
//                .with(rabbitProperties.getRoutingKey());
//    }

//    @Bean
//    Binding binding(Queue codeQueue, FanoutExchange exchange) {
//        return BindingBuilder.bind(codeQueue).to(exchange);
//    }

    @Bean
    public Declarables fanoutBindings() {
        Queue codeQueue = new Queue(rabbitProperties.getCandidateCodeQueue(), true);
        FanoutExchange exchange = new FanoutExchange(rabbitProperties.getExchange());

        return new Declarables(
                codeQueue,
                exchange,
                BindingBuilder.bind(codeQueue).to(exchange));
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
