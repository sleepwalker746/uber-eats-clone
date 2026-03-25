package com.august.restaurant.dto.menucategorydto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MenuCategoryResponseDTO {

    private Long id;
    private String name;
    private String imageUrl;
    private Long restaurantId;
    private String restaurantName;

}
