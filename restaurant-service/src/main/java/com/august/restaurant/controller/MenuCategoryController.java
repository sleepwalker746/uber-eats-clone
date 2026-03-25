package com.august.restaurant.controller;

import com.august.restaurant.dto.menucategorydto.MenuCategoryRequestDTO;
import com.august.restaurant.dto.menucategorydto.MenuCategoryResponseDTO;
import com.august.restaurant.service.interfaces.MenuCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurants/{restaurantId}/categories")
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuCategoryResponseDTO> createCategory(@PathVariable Long restaurantId,
                                                                  @Valid @RequestBody MenuCategoryRequestDTO menuCategoryRequestDTO) {
        log.info("Creating menu category with name : {}", menuCategoryRequestDTO.getName());
        return ResponseEntity.ok(menuCategoryService.createMenuCategory(restaurantId, menuCategoryRequestDTO));
    }

    @GetMapping
    public ResponseEntity<Page<MenuCategoryResponseDTO>> getAllCategories(@PathVariable Long restaurantId,
                                                                          @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(menuCategoryService.getAllMenuCategory(restaurantId, pageable));
    }
}
