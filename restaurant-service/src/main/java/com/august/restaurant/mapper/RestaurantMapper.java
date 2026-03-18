package com.august.restaurant.mapper;

import com.august.restaurant.dto.RestaurantRequestDTO;
import com.august.restaurant.dto.RestaurantResponseDTO;
import com.august.restaurant.entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RestaurantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Restaurant toEntity(RestaurantRequestDTO restaurantRequestDTO);

    RestaurantResponseDTO toDTO(Restaurant restaurant);
}
