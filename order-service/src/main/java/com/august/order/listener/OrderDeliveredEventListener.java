package com.august.order.listener;

import com.august.common.event.OrderDeliveredEvent;
import com.august.common.event.OrderDeliveringEvent;
import com.august.order.entity.Order;
import com.august.order.entity.OrderStatus;
import com.august.order.repository.OrderRepository;
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

    private final OrderRepository orderRepository;

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.delivery.delivered.queue", durable = "true"),
            exchange = @Exchange(value = "delivery.exchange", type = ExchangeTypes.TOPIC),
            key = "delivery.order.delivered"
    ))
    public void handleOrderDeliveredEvent(OrderDeliveredEvent event) {

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Order with id: " + event.orderId() + "not found!"));

        if(OrderStatus.DELIVERED.equals(order.getOrderStatus())) {
            return;
        }

        if(OrderStatus.CANCELED.equals(order.getOrderStatus())) {
            throw new IllegalArgumentException("Order with id: " + event.orderId() + " has been cancelled!");
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

    }

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.delivery.delivering.queue", durable = "true"),
            exchange = @Exchange(value = "delivery.exchange", type = ExchangeTypes.TOPIC),
            key = "delivery.order.delivering"
    ))
    public void handleOrderDeliveringEvent(OrderDeliveringEvent event) {

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Order with id: " + event.orderId() + "not found!"));

        if(OrderStatus.DELIVERING.equals(order.getOrderStatus())) {
            return;
        }

        if(OrderStatus.CANCELED.equals(order.getOrderStatus())) {
            throw new IllegalArgumentException("Order with id: " + event.orderId() + " has been cancelled!");
        }

        order.setOrderStatus(OrderStatus.DELIVERING);
        orderRepository.save(order);

    }



}
