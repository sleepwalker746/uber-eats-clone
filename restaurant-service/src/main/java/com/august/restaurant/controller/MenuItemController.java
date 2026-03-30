package com.august.restaurant.controller;

import com.august.restaurant.dto.menuitemdto.MenuItemRequestDTO;
import com.august.restaurant.dto.menuitemdto.MenuItemResponseDTO;
import com.august.restaurant.dto.menuitemdto.MenuItemUpdateDTO;
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

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping("/categories/{categoryId}/items")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuItemResponseDTO> createMenuItem(@PathVariable Long categoryId,
                                                              @Valid @RequestBody MenuItemRequestDTO menuItemRequestDTO) {
        log.info("Creating food with name : {}", menuItemRequestDTO.getName());
        return ResponseEntity.ok(menuItemService.createMenuItem(categoryId, menuItemRequestDTO));
    }

    @GetMapping("/categories/{categoryId}/items")
    public ResponseEntity<Page<MenuItemResponseDTO>> getAllMenuItems(@PathVariable Long categoryId,
                                                                     @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(menuItemService.getAllMenuItems(categoryId, pageable));
    }
    @PatchMapping("/items/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuItemResponseDTO> updateMenuItem(@PathVariable Long itemId,
                                                              @Valid @RequestBody MenuItemUpdateDTO menuItemUpdateDTO) {
        log.info("Updating food with name : {}", menuItemUpdateDTO.getName());
        return ResponseEntity.ok(menuItemService.updateMenuItem(itemId, menuItemUpdateDTO));
    }
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long itemId) {
        log.info("Deleting food with id : {}", itemId);
        menuItemService.deleteMenuItem(itemId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/items")
    public ResponseEntity<List<MenuItemResponseDTO>> getMenuItemsByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(menuItemService.getMenuItemByIds(ids));
    }
}
