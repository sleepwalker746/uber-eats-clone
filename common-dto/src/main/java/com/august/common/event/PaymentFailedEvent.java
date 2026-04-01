package com.august.common.event;

public record PaymentFailedEvent(
        Long orderId,
        Long restaurantId,
        String reason
) {
}
