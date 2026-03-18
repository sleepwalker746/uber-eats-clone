package com.august.restaurant.repository;

import com.august.restaurant.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem,Long> {
    List<MenuItem> findByCategoryRestaurantId(Long restaurantId);
    List<MenuItem> findByCategoryRestaurantIdAndIsAvailableTrue(Long restaurantId);
    List<MenuItem> findByCategoryIdAndIsAvailableTrue(Long categoryId);
}
