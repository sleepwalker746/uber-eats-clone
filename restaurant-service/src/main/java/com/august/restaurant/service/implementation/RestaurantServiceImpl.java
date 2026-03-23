package com.august.restaurant.service.implementation;

import com.august.common.event.RestaurantRegisteredEvent;
import com.august.restaurant.dto.RestaurantRequestDTO;
import com.august.restaurant.dto.RestaurantResponseDTO;
import com.august.restaurant.entity.Restaurant;
import com.august.restaurant.mapper.RestaurantMapper;
import com.august.restaurant.repository.RestaurantRepository;
import com.august.restaurant.service.interfaces.RestaurantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final RabbitTemplate rabbitTemplate;


    @Transactional
    @Override
    public RestaurantResponseDTO createRestaurant(RestaurantRequestDTO restaurantRequestDTO) {

        Restaurant restaurant = restaurantMapper.toEntity(restaurantRequestDTO);
        restaurant.setIsActive(true);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        RestaurantRegisteredEvent event = new RestaurantRegisteredEvent(
                savedRestaurant.getId(),
                savedRestaurant.getName(),
                savedRestaurant.getEmail()
        );

        rabbitTemplate.convertAndSend(
                "restaurant.exchange",
                "restaurant.registered",
                event
        );

        return restaurantMapper.toDTO(savedRestaurant);
    }

    @Override
    public Page<RestaurantResponseDTO> getAllActiveRestaurants(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Restaurant> restaurantPage = restaurantRepository.findByIsActiveTrue(pageRequest);

        return restaurantPage.map(restaurantMapper::toDTO);
    }

}
