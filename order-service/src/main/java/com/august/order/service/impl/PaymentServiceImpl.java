package com.august.order.service.impl;

import com.august.common.event.OrderPaidEvent;
import com.august.common.event.PaymentCompletedEvent;
import com.august.common.event.PaymentFailedEvent;
import com.august.order.entity.Order;
import com.august.order.entity.OrderStatus;
import com.august.order.repository.OrderRepository;
import com.august.order.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public void handlePaymentCompletedEvent(PaymentCompletedEvent event) {

        log.info("Order №{} was paid", event.orderId());

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + event.orderId()));
        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);

        rabbitTemplate.convertAndSend(
                "order.exchange",
                "order.paid",
                new OrderPaidEvent(
                        order.getId(),
                        order.getRestaurantId(),
                        order.getDeliveryAddress()
                )
        );

    }

    @Override
    @Transactional
    public void handlePaymentFailedEvent(PaymentFailedEvent event) {

        log.error("Error message about payment for the order №{}. Reason: {}", event.orderId(), event.reason());

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + event.orderId()));

        if(order.getOrderStatus() == OrderStatus.CANCELED) {
            return;
        }

        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        log.info("Saga refund has been processed: Status order №{} has been changed on CANCELED", event.orderId());

    }
}
