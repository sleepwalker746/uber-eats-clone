package com.august.restaurant.mapper;

import com.august.restaurant.dto.menuitemdto.MenuItemRequestDTO;
import com.august.restaurant.dto.menuitemdto.MenuItemResponseDTO;
import com.august.restaurant.entity.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MenuItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isAvailable", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    MenuItem toEntity(MenuItemRequestDTO menuItemRequestDTO);

    MenuItemResponseDTO toDto(MenuItem menuItem);
}
