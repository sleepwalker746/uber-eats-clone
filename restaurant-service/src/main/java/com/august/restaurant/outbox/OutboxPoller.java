package com.august.restaurant.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxPoller {

    private final OutboxRepository outboxRepository;
    private final OutboxEventProcessor processor;

    @Scheduled(fixedRate = 5000)
    public void poll () {
        outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.NEW)
                .forEach(processor::processEvent);
    }
}
