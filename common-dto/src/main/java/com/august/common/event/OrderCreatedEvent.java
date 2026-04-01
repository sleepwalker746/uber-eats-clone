package com.august.common.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        Long orderId,
        Long customerId,
        Long restaurantId,
        BigDecimal totalPrice
) {
}
