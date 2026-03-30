package com.august.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RestaurantMenuItemDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Boolean isAvailable;
}
