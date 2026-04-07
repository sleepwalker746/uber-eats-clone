package com.august.restaurant.listener;

import com.august.common.event.OrderCreatedEvent;
import com.august.restaurant.entity.OrderStatus;
import com.august.restaurant.entity.Restaurant;
import com.august.restaurant.entity.RestaurantOrder;
import com.august.restaurant.repository.RestaurantOrderRepository;
import com.august.restaurant.repository.RestaurantRepository;
import com.august.restaurant.service.interfaces.RestaurantOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantListener {

    private final RestaurantOrderService restaurantOrderService;

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "restaurant.order.created.queue", durable = "true"),
            exchange = @Exchange(value = "order.exchange", type = ExchangeTypes.TOPIC),
            key = "order.created"
    ))
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            restaurantOrderService.handleOrderCreatedEvent(event);
        } catch (Exception e) {
            log.error("Error processing OrderPaidEvent {}", event.orderId(), e);
            throw e;
        }
    }
}
