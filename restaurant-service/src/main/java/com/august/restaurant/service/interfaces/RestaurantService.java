package com.august.restaurant.service.interfaces;

import com.august.restaurant.dto.restaurantdto.RestaurantRequestDTO;
import com.august.restaurant.dto.restaurantdto.RestaurantResponseDTO;
import com.august.restaurant.dto.restaurantdto.RestaurantUpdateDTO;
import org.springframework.data.domain.Page;

public interface RestaurantService {

    RestaurantResponseDTO createRestaurant(RestaurantRequestDTO restaurantRequestDTO);
    Page<RestaurantResponseDTO> getAllActiveRestaurants(int page, int size);
    RestaurantResponseDTO updateRestaurant(Long id, RestaurantUpdateDTO restaurantUpdateDTO);
    void deleteRestaurant(Long id);
    RestaurantResponseDTO getRestaurantById(Long id);

}
