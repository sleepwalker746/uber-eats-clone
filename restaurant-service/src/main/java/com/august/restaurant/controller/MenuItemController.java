package com.august.restaurant.controller;

import com.august.restaurant.dto.menuitemdto.MenuItemRequestDTO;
import com.august.restaurant.dto.menuitemdto.MenuItemResponseDTO;
import com.august.restaurant.service.interfaces.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories/{categoryId}/items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<MenuItemResponseDTO> createMenuItem(@PathVariable Long categoryId,
                                                              @Valid @RequestBody MenuItemRequestDTO menuItemRequestDTO) {
        return ResponseEntity.ok(menuItemService.createMenuItem(categoryId, menuItemRequestDTO));
    }
}
