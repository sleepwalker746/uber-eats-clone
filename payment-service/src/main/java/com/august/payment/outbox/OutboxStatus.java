package com.august.payment.outbox;

public enum OutboxStatus {
    NEW,
    PROCESSING,
    PROCESSED,
    FAILED
}
