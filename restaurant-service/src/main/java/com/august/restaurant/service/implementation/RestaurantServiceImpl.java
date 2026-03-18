package com.august.restaurant.service.implementation;

import com.august.restaurant.dto.RestaurantRequestDTO;
import com.august.restaurant.dto.RestaurantResponseDTO;
import com.august.restaurant.entity.Restaurant;
import com.august.restaurant.mapper.RestaurantMapper;
import com.august.restaurant.repository.RestaurantRepository;
import com.august.restaurant.service.interfaces.RestaurantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;


    @Transactional
    @Override
    public RestaurantResponseDTO createRestaurant(RestaurantRequestDTO restaurantRequestDTO) {

        Restaurant restaurant = restaurantMapper.toEntity(restaurantRequestDTO);
        restaurant.setIsActive(true);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return restaurantMapper.toDTO(savedRestaurant);
    }

    @Override
    public Page<RestaurantResponseDTO> getAllActiveRestaurants(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Restaurant> restaurantPage = restaurantRepository.findByIsActiveTrue(pageRequest);

        return restaurantPage.map(restaurantMapper::toDTO);
    }

}
