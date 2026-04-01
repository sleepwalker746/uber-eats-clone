package com.august.common.event;

public record PaymentFailedEvent(
        Long orderId,
        String reason
) {
}
