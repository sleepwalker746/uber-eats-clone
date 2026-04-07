package com.august.delivery.service;

import com.august.common.event.OrderPaidEvent;
import com.august.common.event.OrderPreparingEvent;
import com.august.common.event.OrderReadyEvent;

public interface DeliveryService {
    void assignCourier(Long orderId);
    void completeDelivery(Long orderId);
    void handleOrderPaidEvent(OrderPaidEvent event);
    void handleOrderPreparingEvent(OrderPreparingEvent event);
    void handleOrderReadyEvent(OrderReadyEvent event);
}
