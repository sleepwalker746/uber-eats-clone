package com.august.restaurant.mapper;

import com.august.restaurant.dto.menuitemdto.MenuItemRequestDTO;
import com.august.restaurant.dto.menuitemdto.MenuItemResponseDTO;
import com.august.restaurant.dto.menuitemdto.MenuItemUpdateDTO;
import com.august.restaurant.entity.MenuItem;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MenuItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isAvailable", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    MenuItem toEntity(MenuItemRequestDTO menuItemRequestDTO);

    MenuItemResponseDTO toDto(MenuItem menuItem);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateMenuItem(MenuItemUpdateDTO menuItemUpdateDTO, @MappingTarget MenuItem menuItem);

    MenuItemResponseDTO toMenuItemResponseDTO(MenuItem menuItem);
}
