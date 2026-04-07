package com.august.delivery.listener;

import com.august.common.event.*;
import com.august.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryMessageListener {

    private final DeliveryService deliveryService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "delivery.order.paid.queue", durable = "true"),
            exchange = @Exchange(value = "order.exchange", type = ExchangeTypes.TOPIC),
            key = "order.paid"
    ))
    public void handleOrderPaidEvent(OrderPaidEvent event) {
        try {
            deliveryService.handleOrderPaidEvent(event);
        } catch (Exception e) {
            log.error("Error processing OrderPaidEvent {}", event.orderId(), e);
            throw e;
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "delivery.order.preparing.queue", durable = "true"),
            exchange = @Exchange(value = "restaurant.exchange", type = ExchangeTypes.TOPIC),
            key = "restaurant.order.preparing"
    ))
    public void handleOrderPreparing(OrderPreparingEvent event) {
        try {
            deliveryService.handleOrderPreparingEvent(event);
        } catch (Exception e) {
            log.error("Error processing OrderPaidEvent {}", event.orderId(), e);
            throw e;
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "delivery.order.ready.queue", durable = "true"),
            exchange = @Exchange(value = "restaurant.exchange", type = ExchangeTypes.TOPIC),
            key = "restaurant.order.ready"

    ))
    public void handleOrderReadyEvent(OrderReadyEvent event) {
        try {
            deliveryService.handleOrderReadyEvent(event);
        } catch (Exception e) {
            log.error("Error processing OrderPaidEvent {}", event.orderId(), e);
            throw e;
        }
    }
}
