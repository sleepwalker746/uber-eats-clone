package com.august.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponseDTO {
    private Long id;
    private Long menuItemId;
    private String name;
    private Integer quantity;
    private BigDecimal price;
}
