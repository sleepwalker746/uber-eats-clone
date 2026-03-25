package com.august.restaurant.controller;

import com.august.restaurant.dto.menuitemdto.MenuItemRequestDTO;
import com.august.restaurant.dto.menuitemdto.MenuItemResponseDTO;
import com.august.restaurant.service.interfaces.MenuItemService;
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
@RequestMapping("/api/v1/categories/{categoryId}/items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuItemResponseDTO> createMenuItem(@PathVariable Long categoryId,
                                                              @Valid @RequestBody MenuItemRequestDTO menuItemRequestDTO) {
        log.info("Creating food with name : {}", menuItemRequestDTO.getName());
        return ResponseEntity.ok(menuItemService.createMenuItem(categoryId, menuItemRequestDTO));
    }

    @GetMapping
    public ResponseEntity<Page<MenuItemResponseDTO>> getAllMenuItems(@PathVariable Long categoryId,
                                                                     @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(menuItemService.getAllMenuItems(categoryId, pageable));
    }
}
