package com.august.common.event;

public record OrderPaidEvent(
        Long orderId,
        Long restaurantId,
        String deliveryAddress
) {
}
