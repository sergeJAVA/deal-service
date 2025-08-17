package com.internship.deal_service.config.rabbit;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConsumerConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPrefetchCount(1);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(new SimpleMessageConverter());
        return factory;
    }

    @Bean
    public DirectExchange contractorsContractorExchange() {
        return new DirectExchange("contractors_contractor_exchange", true, false);
    }

    @Bean
    public Queue dealsContractorQueue() {
        return QueueBuilder.durable("deals_contractor_queue")
                .withArgument("x-dead-letter-exchange", "deals_dead_exchange")
                .withArgument("x-dead-letter-routing-key", "dead.contractor")
                .build();
    }

    @Bean
    public DirectExchange dealsDeadExchange() {
        return new DirectExchange("deals_dead_exchange", true, false);
    }

    @Bean
    public Queue dealsDeadContractorQueue() {
        return QueueBuilder.durable("deals_dead_contractor_queue")
                .withArgument("x-message-ttl", 300000)
                .withArgument("x-dead-letter-exchange", "deals_dead_contractor_exchange")
                .withArgument("x-dead-letter-routing-key", "retry.contractor")
                .build();
    }

    @Bean
    public DirectExchange dealsDeadContractorExchange() {
        return new DirectExchange("deals_dead_contractor_exchange", true, false);
    }

    @Bean
    public Binding dealsContractorQueueBinding() {
        return BindingBuilder
                .bind(dealsContractorQueue())
                .to(contractorsContractorExchange())
                .with("contractor.update");
    }

    @Bean
    public Binding dealDeadContractorQueueBinding() {
        return BindingBuilder
                .bind(dealsDeadContractorQueue())
                .to(dealsDeadExchange())
                .with("dead.contractor");
    }

    @Bean
    public Binding retryBinding() {
        return BindingBuilder
                .bind(dealsContractorQueue())
                .to(dealsDeadContractorExchange())
                .with("retry.contractor");
    }

}
