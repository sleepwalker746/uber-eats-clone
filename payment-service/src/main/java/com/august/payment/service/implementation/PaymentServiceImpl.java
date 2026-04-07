package com.august.payment.service.implementation;

import com.august.common.event.OrderCreatedEvent;
import com.august.common.event.PaymentCompletedEvent;
import com.august.common.event.PaymentFailedEvent;
import com.august.payment.entity.Payment;
import com.august.payment.entity.PaymentStatus;
import com.august.payment.outbox.OutboxEvent;
import com.august.payment.outbox.OutboxRepository;
import com.august.payment.outbox.OutboxStatus;
import com.august.payment.repository.PaymentRepository;
import com.august.payment.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    private OutboxEvent buildOutboxEvent(String aggregateType, String aggregateId, String type, Object payload) {
        try {
            return OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .type(type)
                    .payload(objectMapper.writeValueAsString(payload))
                    .status(OutboxStatus.NEW)
                    .retryCount(0)
                    .createdAt(Instant.now())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event: " + type, e);
        }
    }

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

        OutboxEvent outboxEvent;

        if (Math.random() < 0.2) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Not enough funds on the card");
            log.info("Payment failed for order {}: {}", event.orderId(), payment.getFailureReason());

            outboxEvent = buildOutboxEvent(
                    "Payment",
                    payment.getOrderId().toString(),
                    "PaymentFailedEvent",
                    new PaymentFailedEvent(event.orderId(), payment.getRestaurantId(), payment.getFailureReason())
            );

        } else {

            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            log.info("Payment completed for order {}", event.orderId());

            outboxEvent = buildOutboxEvent(
                    "Payment",
                    payment.getOrderId().toString(),
                    "PaymentCompletedEvent",
                    new PaymentCompletedEvent(event.restaurantId(), event.orderId())
            );
        }

        paymentRepository.save(payment);
        outboxRepository.save(outboxEvent);

    }
}
