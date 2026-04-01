package com.august.common.event;

public record OrderPreparingEvent(
        Long orderId,
        Long restaurantId
)
{ }
