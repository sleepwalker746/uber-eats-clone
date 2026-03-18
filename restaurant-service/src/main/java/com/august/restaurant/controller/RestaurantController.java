package com.august.restaurant.controller;

import com.august.restaurant.dto.RestaurantRequestDTO;
import com.august.restaurant.dto.RestaurantResponseDTO;
import com.august.restaurant.service.interfaces.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantResponseDTO> register(@Valid @RequestBody RestaurantRequestDTO restaurantRequestDTO) {
        log.info("Register restaurant with name : {}", restaurantRequestDTO.getName());
        RestaurantResponseDTO restaurantResponseDTO = restaurantService.createRestaurant(restaurantRequestDTO);
        return ResponseEntity.ok(restaurantResponseDTO);
    }


}
