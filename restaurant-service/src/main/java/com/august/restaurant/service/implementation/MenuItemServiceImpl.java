package com.august.restaurant.service.implementation;

import com.august.restaurant.dto.menuitemdto.MenuItemRequestDTO;
import com.august.restaurant.dto.menuitemdto.MenuItemResponseDTO;
import com.august.restaurant.dto.menuitemdto.MenuItemUpdateDTO;
import com.august.restaurant.entity.MenuCategory;
import com.august.restaurant.entity.MenuItem;
import com.august.restaurant.mapper.MenuItemMapper;
import com.august.restaurant.repository.MenuCategoryRepository;
import com.august.restaurant.repository.MenuItemRepository;
import com.august.restaurant.service.interfaces.MenuItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
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
        return menuItemMapper.toMenuItemResponseDTO(savedMenuItem);

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

    @Override
    @Transactional
    public MenuItemResponseDTO updateMenuItem(Long id, MenuItemUpdateDTO menuItemUpdateDTO) {
        log.info("Update menu item id : {}", id);
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with id: " + id));
        menuItemMapper.updateMenuItem(menuItemUpdateDTO, menuItem);
        menuItem = menuItemRepository.save(menuItem);
        return menuItemMapper.toMenuItemResponseDTO(menuItem);
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long id) {
        log.info("Delete menu item id : {}", id);
        if (!menuItemRepository.existsById(id)) {
            throw new IllegalArgumentException("Menu item not found with id: " + id);
        }
        menuItemRepository.deleteById(id);

    }

    @Override
    public List<MenuItemResponseDTO> getMenuItemByIds(List<Long> ids) {
        List<MenuItem> menuItems = menuItemRepository.findByIdIn(ids);
        return menuItemMapper.toDtoList(menuItems);
    }
}
