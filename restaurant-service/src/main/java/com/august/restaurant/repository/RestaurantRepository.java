package com.august.restaurant.repository;

import com.august.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {
    Page<Restaurant> findByIsActiveTrue(Pageable pageable);
    boolean existsByName(String name);
    boolean existsByPhone(String phone);
}
