package com.august.order.outbox;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OutboxEventProcessor {
    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final int MAX_RETRY_COUNT = 3;

    @Transactional
    public void processEvent(OutboxEvent event) {
        event.setStatus(OutboxStatus.PROCESSING);
        outboxRepository.save(event);

        try {
            rabbitTemplate.convertAndSend(
                    event.getType(),
                    "",
                    event.getPayload()
            );
            event.setStatus(OutboxStatus.PROCESSED);
            event.setProcessedAt(Instant.now());
        } catch (Exception e) {

            event.setRetryCount(event.getRetryCount() + 1);

            if (event.getRetryCount() >= MAX_RETRY_COUNT) {
                event.setStatus(OutboxStatus.FAILED);
            } else {
                event.setStatus(OutboxStatus.NEW);
            }
        }
        outboxRepository.save(event);
    }
}
