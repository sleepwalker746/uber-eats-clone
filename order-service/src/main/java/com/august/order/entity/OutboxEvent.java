package com.august.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Table(name = "outbox_event")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    private UUID id;

    private String aggregateType;
    private String aggregateId;
    private String type;

    @Column(columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private Instant createdAt;
    private Instant processedAt;

}
