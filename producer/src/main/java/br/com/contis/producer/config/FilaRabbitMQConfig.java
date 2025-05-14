package br.com.contis.producer.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilaRabbitMQConfig {

    public static final String NOME_FILA = "bling-orders";
    public static final String NOME_EXCHANGE = "bling-orders-exchange";
    public static final String ROUTING_KEY = "bling-orders-routing-key";


    @Bean
    DirectExchange orderExchange() {
        return new DirectExchange(NOME_EXCHANGE);
    }

    @Bean
    Queue ordersQueue() {
        return QueueBuilder.durable(NOME_FILA).build();
    }

    @Bean
    Binding ordersBinding() {
        return BindingBuilder.bind(ordersQueue()).to(orderExchange()).with(ROUTING_KEY);
    }
}
