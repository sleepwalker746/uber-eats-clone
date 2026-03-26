package com.august.restaurant.service.implementation;

import com.august.common.event.RestaurantRegisteredEvent;
import com.august.restaurant.dto.restaurantdto.RestaurantRequestDTO;
import com.august.restaurant.dto.restaurantdto.RestaurantResponseDTO;
import com.august.restaurant.dto.restaurantdto.RestaurantUpdateDTO;
import com.august.restaurant.entity.Restaurant;
import com.august.restaurant.mapper.RestaurantMapper;
import com.august.restaurant.repository.RestaurantRepository;
import com.august.restaurant.service.interfaces.RestaurantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final RabbitTemplate rabbitTemplate;


    @Transactional
    @Override
    public RestaurantResponseDTO createRestaurant(RestaurantRequestDTO restaurantRequestDTO) {

        if (restaurantRepository.existsByName(restaurantRequestDTO.getName())) {
            throw new IllegalArgumentException("Restaurant already exists");
        }

        if (restaurantRequestDTO.getPhone() != null && restaurantRepository.existsByPhone(restaurantRequestDTO.getPhone())) {
            throw new IllegalArgumentException("Phone already exists");
        }

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

    @Override
    public RestaurantResponseDTO updateRestaurant(Long id, RestaurantUpdateDTO restaurantUpdateDTO) {
        log.info("Update Restaurant by id {}", id);
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant with id not found: " + id));
        restaurantMapper.updateRestaurant(restaurantUpdateDTO, restaurant);
        restaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toResponseDTO(restaurant);
    }

    @Override
    @Transactional
    public void deleteRestaurant(Long id) {
        log.info("Delete Restaurant by id {}", id);
        if (!restaurantRepository.existsById(id)) {
            throw new IllegalArgumentException("Restaurant with id not found: " + id);
        }
        restaurantRepository.deleteById(id);
    }
}
