package com.august.restaurant.service.interfaces;

import com.august.restaurant.dto.menucategorydto.MenuCategoryRequestDTO;
import com.august.restaurant.dto.menucategorydto.MenuCategoryResponseDTO;

public interface MenuCategoryService {

    MenuCategoryResponseDTO createMenuCategory(Long restaurantId, MenuCategoryRequestDTO menuCategoryRequestDTO);

}
