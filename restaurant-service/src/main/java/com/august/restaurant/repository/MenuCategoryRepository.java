package com.august.restaurant.repository;

import com.august.restaurant.entity.MenuCategory;
import com.august.restaurant.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory,Long> {
    List<MenuCategory> findByRestaurantId(Long restaurantId);
    Page<MenuCategory> findAllByRestaurantId(Long restaurantId, Pageable pageable);
}
