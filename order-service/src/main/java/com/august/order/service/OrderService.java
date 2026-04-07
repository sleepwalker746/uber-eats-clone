package com.august.order.service;

import com.august.common.event.OrderDeliveredEvent;
import com.august.common.event.OrderDeliveringEvent;
import com.august.order.dto.OrderRequestDTO;
import com.august.order.dto.OrderResponseDTO;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO);
    void handleOrderDeliveringEvent(OrderDeliveringEvent event);
    void handleOrderDeliveredEvent(OrderDeliveredEvent event);
}
