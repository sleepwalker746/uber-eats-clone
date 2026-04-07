package com.august.order.service;

import com.august.common.event.PaymentCompletedEvent;
import com.august.common.event.PaymentFailedEvent;

public interface PaymentService {
    void handlePaymentFailedEvent(PaymentFailedEvent event);
    void handlePaymentCompletedEvent(PaymentCompletedEvent event);
}
