package com.august.restaurant.listener;

import com.august.common.event.PaymentCompletedEvent;
import com.august.common.event.PaymentFailedEvent;
import com.august.restaurant.service.interfaces.PaymentService;
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
public class PaymentEventListener {

    private final PaymentService paymentService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "restaurant.payment.completed.queue", durable = "true"),
            exchange = @Exchange(value = "payment.exchange", type = ExchangeTypes.TOPIC),
            key = "payment.completed"
    ))
    public void handlePaymentConfirmed(PaymentCompletedEvent event) {
        log.info("Received PaymentCompletedEvent {}", event.orderId());

        try {
            paymentService.handlePaymentCompletedEvent(event);
        } catch (Exception e) {
            log.error("Error processing PaymentCompletedEvent {}", event.orderId(), e);
            throw e;
        }

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "restaurant.payment.failed.queue", durable = "true"),
            exchange = @Exchange(value = "payment.exchange", type = ExchangeTypes.TOPIC),
            key = "payment.failed"
    ))
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("Received PaymentFailedEvent {}", event.orderId());

        try {
            paymentService.handlePaymentFailedEvent(event);
        } catch (Exception e) {
            log.error("Error processing PaymentFailedEvent {}", event.orderId(), e);
            throw e;
        }
    }
}
