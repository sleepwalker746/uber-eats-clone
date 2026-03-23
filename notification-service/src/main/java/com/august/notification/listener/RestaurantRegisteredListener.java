package com.august.notification.listener;

import com.august.common.event.RestaurantRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RestaurantRegisteredListener {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "notification.restaurant.registered.queue", durable = "true"),
            exchange = @Exchange(value = "restaurant.exchange", type = ExchangeTypes.TOPIC),
            key = "restaurant.registered"
    ))
    public void handle(RestaurantRegisteredEvent event) {
        log.info("Got an event from RabbitMQ");
        log.info("Mail to email: {} of restaurant's registration '{}' (ID:{})",
                event.email(), event.name(), event.restaurantId());
    }
}
