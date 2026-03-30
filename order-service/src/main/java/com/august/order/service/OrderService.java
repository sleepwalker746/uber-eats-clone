package com.august.order.service;

import com.august.order.dto.OrderRequestDTO;
import com.august.order.dto.OrderResponseDTO;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO);
}
