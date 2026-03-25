package com.august.restaurant.dto.restaurantdto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class RestaurantResponseDTO {

    private Long id;
    private String name;
    private String address;
    private Boolean isActive;
    private String phone;
    private String email;
    private String imageUrl;

}
