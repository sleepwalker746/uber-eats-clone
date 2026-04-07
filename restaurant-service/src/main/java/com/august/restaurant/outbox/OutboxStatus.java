package com.august.restaurant.outbox;

public enum OutboxStatus {
    NEW,
    PROCESSING,
    PROCESSED,
    FAILED
}
