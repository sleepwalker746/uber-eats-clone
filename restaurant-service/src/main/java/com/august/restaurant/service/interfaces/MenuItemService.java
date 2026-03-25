package com.august.restaurant.service.interfaces;

import com.august.restaurant.dto.menuitemdto.MenuItemRequestDTO;
import com.august.restaurant.dto.menuitemdto.MenuItemResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuItemService {

    MenuItemResponseDTO createMenuItem(Long categoryId, MenuItemRequestDTO menuItemRequestDTO);
    Page<MenuItemResponseDTO> getAllMenuItems(Long categoryId, Pageable pageable);

}
