package com.august.order.dto;

import com.august.order.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponseDTO {

    private Long id;
    private Long restaurantId;
    private String pickupAddress;
    private String deliveryAddress;

    private BigDecimal itemsPrice;
    private BigDecimal deliveryPrice;
    private BigDecimal totalPrice;

    private OrderStatus orderStatus;
    private ZonedDateTime createdAt;

    private List<OrderItemResponseDTO> orderItems;

}
