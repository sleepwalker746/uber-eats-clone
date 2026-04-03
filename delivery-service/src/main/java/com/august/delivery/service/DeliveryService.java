package com.august.delivery.service;

public interface DeliveryService {
    void assignCourier(Long orderId);
    void completeDelivery(Long orderId);
}
