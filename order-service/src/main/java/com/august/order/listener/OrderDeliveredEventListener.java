package com.august.order.listener;

import com.august.common.event.OrderDeliveredEvent;
import com.august.common.event.OrderDeliveringEvent;
import com.august.order.service.OrderService;
import jakarta.transaction.Transactional;
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
public class OrderDeliveredEventListener {

    private final OrderService orderService;

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.delivery.delivering.queue", durable = "true"),
            exchange = @Exchange(value = "delivery.exchange", type = ExchangeTypes.TOPIC),
            key = "delivery.order.delivering"
    ))
    public void handleOrderDeliveringEvent(OrderDeliveringEvent event) {

        orderService.handleOrderDeliveringEvent(event);

    }

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.delivery.delivered.queue", durable = "true"),
            exchange = @Exchange(value = "delivery.exchange", type = ExchangeTypes.TOPIC),
            key = "delivery.order.delivered"
    ))
    public void handleOrderDeliveredEvent(OrderDeliveredEvent event) {

        orderService.handleOrderDeliveredEvent(event);

    }

}
