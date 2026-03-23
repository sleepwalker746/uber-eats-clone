package com.august.common.event;

public record RestaurantRegisteredEvent(
        Long restaurantId,
        String name,
        String email
) { }
