package com.august.restaurant.service.interfaces;

import com.august.restaurant.dto.RestaurantRequestDTO;
import com.august.restaurant.dto.RestaurantResponseDTO;
import org.springframework.data.domain.Page;

public interface RestaurantService {

    RestaurantResponseDTO createRestaurant(RestaurantRequestDTO restaurantRequestDTO);

    Page<RestaurantResponseDTO> getAllActiveRestaurants(int page, int size);

}
