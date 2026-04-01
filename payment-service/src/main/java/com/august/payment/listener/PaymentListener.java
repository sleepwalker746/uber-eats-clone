package com.august.payment.listener;

import com.august.common.event.OrderCreatedEvent;
import com.august.common.event.PaymentCompletedEvent;
import com.august.common.event.PaymentFailedEvent;
import com.august.payment.entity.Payment;
import com.august.payment.entity.PaymentStatus;
import com.august.payment.repository.PaymentRepository;
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

import java.util.UUID;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class PaymentListener {

    private final RabbitTemplate rabbitTemplate;
    private final PaymentRepository paymentRepository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "payment.order.created.queue", durable = "true"),
            exchange = @Exchange(value = "order.exchange", type = ExchangeTypes.TOPIC),
            key = "order.created"
    ))
    public void handlePayment(OrderCreatedEvent event) {
        log.info("Есть контакт");

      if (paymentRepository.findByOrderId(event.orderId()).isPresent()) {
          log.warn("Order №{} is already payed", event.orderId());
          return;
      }

      Payment payment = Payment.builder()
              .orderId(event.orderId())
              .userId(event.customerId())
              .restaurantId(event.restaurantId())
              .totalPrice(event.totalPrice())
              .transactionId(UUID.randomUUID().toString())
              .build();

    if (Math.random() < 0.2 ) {
        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setFailureReason("Not enough funds on the card");
        paymentRepository.save(payment);
        log.info("ПЛОХО НАСРАЛ");
        rabbitTemplate.convertAndSend("payment.exchange", "payment.failed",
                new PaymentFailedEvent(event.orderId(), payment.getRestaurantId(), payment.getFailureReason()));

    } else {
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        log.info("НАСРАЛ");
        rabbitTemplate.convertAndSend("payment.exchange", "payment.completed",
                new PaymentCompletedEvent(event.restaurantId(), event.orderId()));

        }
    }
}
