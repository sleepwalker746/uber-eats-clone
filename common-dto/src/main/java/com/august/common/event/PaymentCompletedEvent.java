package com.august.common.event;

public record PaymentCompletedEvent(
        Long orderId
) {
}
