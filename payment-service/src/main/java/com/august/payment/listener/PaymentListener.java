package com.august.payment.listener;

import com.august.common.event.OrderCreatedEvent;
import com.august.payment.service.PaymentService;
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
@Transactional
@RequiredArgsConstructor
public class PaymentListener {

    private final PaymentService paymentService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "payment.order.created.queue", durable = "true"),
            exchange = @Exchange(value = "order.exchange", type = ExchangeTypes.TOPIC),
            key = "order.created"
    ))
    public void handlePayment(OrderCreatedEvent event) {

        paymentService.handlePayment(event);

    }
}
