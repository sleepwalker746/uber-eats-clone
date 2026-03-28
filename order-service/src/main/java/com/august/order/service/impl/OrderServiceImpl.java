package com.august.order.service.impl;

import com.august.order.dto.OrderRequestDTO;
import com.august.order.entity.Order;
import com.august.order.entity.OrderStatus;
import com.august.order.mapper.OrderMapper;
import com.august.order.repository.OrderRepository;
import com.august.order.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public Long createOrder(OrderRequestDTO orderRequestDTO) {

        Order order = orderMapper.toEntity(orderRequestDTO);
        order.setOrderStatus(OrderStatus.CREATED);


        return 0L;
    }



}
