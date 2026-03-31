package com.august.restaurant.controller;

import com.august.restaurant.dto.restaurantdto.RestaurantRequestDTO;
import com.august.restaurant.dto.restaurantdto.RestaurantResponseDTO;
import com.august.restaurant.dto.restaurantdto.RestaurantUpdateDTO;
import com.august.restaurant.service.interfaces.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponseDTO> register(
            @Valid @RequestBody RestaurantRequestDTO restaurantRequestDTO) {
        log.info("Registering restaurant with name : {}", restaurantRequestDTO.getName());
        RestaurantResponseDTO restaurantResponseDTO = restaurantService.createRestaurant(restaurantRequestDTO);
        return ResponseEntity.ok(restaurantResponseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantResponseDTO>> getAllRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Fetching restaurants with page {} and size {}", page, size);
        Page<RestaurantResponseDTO> responsePage = restaurantService.getAllActiveRestaurants(page, size);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantById(@PathVariable Long restaurantId) {
        log.info("Fetching restaurant with id {}", restaurantId);
        RestaurantResponseDTO restaurantResponseDTO = restaurantService.getRestaurantById(restaurantId);
        return ResponseEntity.ok(restaurantResponseDTO);
    }

    @PatchMapping("/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponseDTO> updateRestaurant(
            @PathVariable Long restaurantId,
            @RequestBody RestaurantUpdateDTO restaurantUpdateDTO) {
        log.info("Updating restaurant with id {}", restaurantUpdateDTO.getName());
        return  ResponseEntity.ok(restaurantService.updateRestaurant(restaurantId, restaurantUpdateDTO));
    }

    @DeleteMapping("/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long restaurantId) {
        log.info("Deleting restaurant with id {}", restaurantId);
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }


}
