package com.august.restaurant.dto.menuitemdto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MenuItemResponseDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean isAvailable;
    private String imageUrl;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private Long categoryId;
    private String categoryName;

}
