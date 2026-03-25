package com.august.restaurant.dto.menucategorydto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MenuCategoryResponseDTO {

    private Long id;
    private String name;
    private String imageUrl;
    private Long restaurantId;
    private String restaurantName;

}
