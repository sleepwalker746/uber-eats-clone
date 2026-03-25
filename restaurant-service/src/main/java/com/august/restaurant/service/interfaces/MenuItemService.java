package com.august.restaurant.service.interfaces;

import com.august.restaurant.dto.menuitemdto.MenuItemRequestDTO;
import com.august.restaurant.dto.menuitemdto.MenuItemResponseDTO;

public interface MenuItemService {

    MenuItemResponseDTO createMenuItem(Long categoryId, MenuItemRequestDTO menuItemRequestDTO);

}
