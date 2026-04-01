package com.august.restaurant.repository;

import com.august.restaurant.entity.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {
    Boolean existsByOrderId(Long orderId);
    Optional<RestaurantOrder> findByOrderId(Long orderId);
}
