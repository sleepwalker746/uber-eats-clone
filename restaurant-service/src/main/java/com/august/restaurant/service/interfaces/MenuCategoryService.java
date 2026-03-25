package com.august.restaurant.service.interfaces;

import com.august.restaurant.dto.menucategorydto.MenuCategoryRequestDTO;
import com.august.restaurant.dto.menucategorydto.MenuCategoryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuCategoryService {

    MenuCategoryResponseDTO createMenuCategory(Long restaurantId, MenuCategoryRequestDTO menuCategoryRequestDTO);
    Page<MenuCategoryResponseDTO> getAllMenuCategory(Long restaurantId, Pageable pageable);

}
