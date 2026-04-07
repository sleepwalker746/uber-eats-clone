package com.august.restaurant.service.interfaces;

import com.august.common.event.PaymentCompletedEvent;
import com.august.common.event.PaymentFailedEvent;

public interface PaymentService {
    void handlePaymentCompletedEvent(PaymentCompletedEvent event);
    void handlePaymentFailedEvent(PaymentFailedEvent event);
}
