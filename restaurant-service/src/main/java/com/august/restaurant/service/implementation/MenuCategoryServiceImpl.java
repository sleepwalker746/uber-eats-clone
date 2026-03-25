package com.august.restaurant.service.implementation;

import com.august.restaurant.dto.menucategorydto.MenuCategoryRequestDTO;
import com.august.restaurant.dto.menucategorydto.MenuCategoryResponseDTO;
import com.august.restaurant.entity.MenuCategory;
import com.august.restaurant.entity.Restaurant;
import com.august.restaurant.mapper.MenuCategoryMapper;
import com.august.restaurant.repository.MenuCategoryRepository;
import com.august.restaurant.repository.RestaurantRepository;
import com.august.restaurant.service.interfaces.MenuCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuCategoryServiceImpl implements MenuCategoryService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryMapper menuCategoryMapper;


    @Override
    public MenuCategoryResponseDTO createMenuCategory(Long restaurantId, MenuCategoryRequestDTO menuCategoryRequestDTO) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with id: !" + restaurantId));


        MenuCategory menuCategory = menuCategoryMapper.toEntity(menuCategoryRequestDTO);
        menuCategory.setRestaurant(restaurant);

        MenuCategory savedMenuCategory = menuCategoryRepository.save(menuCategory);
        return menuCategoryMapper.toDto(savedMenuCategory);

    }

    @Override
    public Page<MenuCategoryResponseDTO> getAllMenuCategory(Long restaurantId, Pageable pageable) {
        return menuCategoryRepository.findAllByRestaurantId(restaurantId, pageable)
                .map(menuCategory -> MenuCategoryResponseDTO.builder()
                        .id(menuCategory.getId())
                        .name(menuCategory.getName())
                        .imageUrl(menuCategory.getImageUrl())
                .build());

    }
}
