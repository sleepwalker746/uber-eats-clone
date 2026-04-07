package com.august.order.outbox;

public enum OutboxStatus {
    NEW,
    PROCESSING,
    PROCESSED,
    FAILED
}
