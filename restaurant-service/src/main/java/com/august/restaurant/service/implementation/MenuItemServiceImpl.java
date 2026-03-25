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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuItemMapper menuItemMapper;

    @Override
    @Transactional
    public MenuItemResponseDTO createMenuItem(Long categoryId, MenuItemRequestDTO menuItemRequestDTO) {

        MenuCategory menuCategory = menuCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: !" + categoryId));

        MenuItem menuItem = menuItemMapper.toEntity(menuItemRequestDTO);
        menuItem.setCategory(menuCategory);
        menuItem.setIsAvailable(true);

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return menuItemMapper.toDto(savedMenuItem);

    }

    @Override
    public Page<MenuItemResponseDTO> getAllMenuItems(Long categoryId, Pageable pageable) {
        return menuItemRepository.findAllByCategoryId(categoryId, pageable)
                .map(item -> MenuItemResponseDTO.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .price(item.getPrice())
                        .isAvailable(item.getIsAvailable())
                        .build()
                );
    }
}
