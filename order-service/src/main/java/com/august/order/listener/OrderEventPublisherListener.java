package com.august.order.listener;

import com.august.common.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventPublisherListener {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener
    public void publishOrderCreatedEventToRabbit(OrderCreatedEvent event) {

        log.info("Order transaction #{} has been successfully completed. Sending to RabbitMQ...", event.orderId());

        rabbitTemplate.convertAndSend(
                "order.exchange",
                "order.created",
                event
        );

        log.info("Event order.created has been successfully submitted!");

    }
}
