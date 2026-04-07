package com.august.restaurant.service.interfaces;

import com.august.common.event.OrderCreatedEvent;
import com.august.common.event.OrderPaidEvent;

public interface RestaurantOrderService {
    void markOrderAsReady(Long orderId);
    void handleOrderCreatedEvent(OrderCreatedEvent event);
}
