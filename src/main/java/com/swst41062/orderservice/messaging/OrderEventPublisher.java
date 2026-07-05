package com.swst41062.orderservice.messaging;

import com.swst41062.orderservice.dto.OrderEventMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate,
                                @Value("${messaging.order-events.exchange}") String exchangeName,
                                @Value("${messaging.order-events.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    public void publishOrderCreated(OrderEventMessage event) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
    }
}
