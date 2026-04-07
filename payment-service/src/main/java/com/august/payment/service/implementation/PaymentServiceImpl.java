package com.august.payment.service.implementation;

import com.august.common.event.OrderCreatedEvent;
import com.august.common.event.PaymentCompletedEvent;
import com.august.common.event.PaymentFailedEvent;
import com.august.payment.entity.Payment;
import com.august.payment.entity.PaymentStatus;
import com.august.payment.repository.PaymentRepository;
import com.august.payment.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public void handlePayment(OrderCreatedEvent event) {
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
            log.info("Payment failed for order {}: {}", event.orderId(), payment.getFailureReason());
            rabbitTemplate.convertAndSend("payment.exchange", "payment.failed",
                    new PaymentFailedEvent(event.orderId(), payment.getRestaurantId(), payment.getFailureReason()));

        } else {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            log.info("Payment completed for order {}", event.orderId());
            rabbitTemplate.convertAndSend("payment.exchange", "payment.completed",
                    new PaymentCompletedEvent(event.restaurantId(), event.orderId()));

        }
    }
}
