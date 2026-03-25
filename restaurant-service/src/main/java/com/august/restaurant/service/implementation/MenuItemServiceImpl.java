package com.august.restaurant.service.implementation;

import com.august.restaurant.dto.menuitemdto.MenuItemRequestDTO;
import com.august.restaurant.dto.menuitemdto.MenuItemResponseDTO;
import com.august.restaurant.entity.MenuCategory;
import com.august.restaurant.entity.MenuItem;
import com.august.restaurant.mapper.MenuItemMapper;
import com.august.restaurant.repository.MenuCategoryRepository;
import com.august.restaurant.repository.MenuItemRepository;
import com.august.restaurant.service.interfaces.MenuItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private MenuItemRepository menuItemRepository;
    private MenuCategoryRepository menuCategoryRepository;
    private MenuItemMapper menuItemMapper;

    @Override
    @Transactional
    public MenuItemResponseDTO createMenuItem(Long categoryId, MenuItemRequestDTO menuItemRequestDTO) {

        MenuCategory menuCategory = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: !" + categoryId));

        MenuItem menuItem = menuItemMapper.toEntity(menuItemRequestDTO);
        menuItem.setCategory(menuCategory);

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return menuItemMapper.toDto(savedMenuItem);

    }
}
