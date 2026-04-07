package com.august.payment.service;

import com.august.common.event.OrderCreatedEvent;

public interface PaymentService {
    void handlePayment(OrderCreatedEvent event);
}
