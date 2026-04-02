package com.august.common.event;

public record OrderReadyEvent(
        Long orderId,
        Long restaurantId
) {
}
