package com.august.restaurant.service.interfaces;

import com.august.restaurant.dto.RestaurantRequestDTO;
import com.august.restaurant.dto.RestaurantResponseDTO;

public interface RestaurantService {
    RestaurantResponseDTO createRestaurant(RestaurantRequestDTO restaurantRequestDTO);
}
