package com.august.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantResponseDTO {

    Long id;
    String name;
    String address;
    Boolean isActive;
    String phone;
    String email;
    String imageUrl;
    ZonedDateTime createdAt;

}
