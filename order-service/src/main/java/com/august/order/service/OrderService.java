package com.august.order.service;

import com.august.order.dto.OrderRequestDTO;

public interface OrderService {
    Long createOrder(OrderRequestDTO orderRequestDTO);
}
