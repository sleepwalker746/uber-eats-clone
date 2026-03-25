package com.august.restaurant.dto.menucategorydto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MenuCategoryRequestDTO {

    private String name;
    private Long restaurantId;

}
