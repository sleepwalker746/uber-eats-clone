package com.august.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemRequestDTO {

    @NotNull
    private Long menuItemId;

    @NotNull
    @Min(value = 1)
    private Integer quantity;

}
