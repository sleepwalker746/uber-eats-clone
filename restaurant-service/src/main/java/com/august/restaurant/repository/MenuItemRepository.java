package com.august.restaurant.repository;

import com.august.restaurant.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem,Long> {
    List<MenuItem> findByCategoryRestaurantId(Long restaurantId);
    List<MenuItem> findByCategoryRestaurantIdAndIsAvailableTrue(Long restaurantId);
    List<MenuItem> findByCategoryIdAndIsAvailableTrue(Long categoryId);
    Page<MenuItem> findAllByCategoryId(Long categoryId, Pageable pageable);
}
