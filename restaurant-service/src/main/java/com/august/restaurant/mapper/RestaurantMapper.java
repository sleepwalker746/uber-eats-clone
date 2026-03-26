package com.august.restaurant.mapper;

import com.august.restaurant.dto.restaurantdto.RestaurantRequestDTO;
import com.august.restaurant.dto.restaurantdto.RestaurantResponseDTO;
import com.august.restaurant.dto.restaurantdto.RestaurantUpdateDTO;
import com.august.restaurant.entity.Restaurant;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RestaurantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Restaurant toEntity(RestaurantRequestDTO restaurantRequestDTO);

    RestaurantResponseDTO toDTO(Restaurant restaurant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateRestaurant(RestaurantUpdateDTO restaurantUpdateDTO, @MappingTarget Restaurant restaurant);

    RestaurantResponseDTO toResponseDTO(Restaurant restaurant);
}
