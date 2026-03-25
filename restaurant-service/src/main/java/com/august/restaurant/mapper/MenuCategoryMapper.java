package com.august.restaurant.mapper;

import com.august.restaurant.dto.menucategorydto.MenuCategoryRequestDTO;
import com.august.restaurant.dto.menucategorydto.MenuCategoryResponseDTO;
import com.august.restaurant.entity.MenuCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MenuCategoryMapper {

    @Mapping(target = "id", ignore = true)
    MenuCategory toEntity(MenuCategoryRequestDTO menuCategoryRequestDTO);

    MenuCategoryResponseDTO toDto(MenuCategory menuCategory);


}
