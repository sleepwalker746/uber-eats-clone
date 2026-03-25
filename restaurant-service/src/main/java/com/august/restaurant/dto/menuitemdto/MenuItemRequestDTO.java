package com.august.restaurant.dto.menuitemdto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class MenuItemRequestDTO {

    @NotBlank(message = "Name should not be empty!")
    private String name;

    @Size(max = 1000, message = "Description cannot be longer than 1000 characters!")
    private String description;

    @NotNull
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    @Positive(message = "Price should be more than 0")
    private BigDecimal price;

    @NotNull(message = "Category ID is required!")
    private Long categoryId;


}
