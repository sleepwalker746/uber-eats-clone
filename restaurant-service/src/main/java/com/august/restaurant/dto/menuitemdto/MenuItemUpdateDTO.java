package com.august.restaurant.dto.menuitemdto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MenuItemUpdateDTO {

    private String name;
    private String description;
    @Positive
    private BigDecimal price;
    private Boolean isAvailable;
    private Long categoryId;

}
