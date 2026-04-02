package com.august.order.listener;

import com.august.common.event.OrderPaidEvent;
import com.august.common.event.PaymentCompletedEvent;
import com.august.common.event.PaymentFailedEvent;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.payment.failed.queue", durable = "true"),
            exchange = @Exchange(value = "payment.exchange", type = ExchangeTypes.TOPIC),
            key = "payment.failed"
    ))
    public void handlePaymentFailed(PaymentFailedEvent event) {

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

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.payment.completed.queue", durable = "true"),
            exchange = @Exchange(value = "payment.exchange", type = ExchangeTypes.TOPIC),
            key = "payment.completed"
    ))
    public void handlePaymentSuccess(PaymentCompletedEvent event) {
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

}
