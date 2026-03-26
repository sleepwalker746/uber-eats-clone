package com.august.restaurant.dto.restaurantdto;

import lombok.Getter;

@Getter
public class RestaurantUpdateDTO {

    private String name;
    private String description;
    private boolean isActive;
    private String address;
    private String email;
    private String phone;
    private String imageUrl;

}
