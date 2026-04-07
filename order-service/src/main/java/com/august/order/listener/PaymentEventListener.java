package com.august.order.listener;

import com.august.common.event.PaymentCompletedEvent;
import com.august.common.event.PaymentFailedEvent;
import com.august.order.service.PaymentService;
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
public class PaymentEventListener {

    private final PaymentService paymentService;

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.payment.failed.queue", durable = "true"),
            exchange = @Exchange(value = "payment.exchange", type = ExchangeTypes.TOPIC),
            key = "payment.failed"
    ))
    public void handlePaymentFailed(PaymentFailedEvent event) {

        paymentService.handlePaymentFailedEvent(event);

        }

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.payment.completed.queue", durable = "true"),
            exchange = @Exchange(value = "payment.exchange", type = ExchangeTypes.TOPIC),
            key = "payment.completed"
    ))
    public void handlePaymentCompleted(PaymentCompletedEvent event) {

        paymentService.handlePaymentCompletedEvent(event);

    }

}
