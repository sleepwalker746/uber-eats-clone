package com.august.order.service.impl;

import com.august.order.client.RestaurantClient;
import com.august.order.dto.OrderRequestDTO;
import com.august.order.dto.OrderResponseDTO;
import com.august.order.dto.RestaurantMenuItemDTO;
import com.august.order.entity.Order;
import com.august.order.entity.OrderItem;
import com.august.order.entity.OrderStatus;
import com.august.order.mapper.OrderMapper;
import com.august.order.repository.OrderRepository;
import com.august.order.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {


    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final RestaurantClient  restaurantClient;
    private static final BigDecimal DELIVERY_PRICE = BigDecimal.valueOf(1);

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
            Object userIdClaim = jwt.getClaim("userId");

            if (userIdClaim != null) {
                return Long.valueOf(userIdClaim.toString());
            }
        }
        throw new RuntimeException("Не удалось найти userId в токене. Перелогиньтесь!");
    }

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {

        BigDecimal itemsTotalPrice = BigDecimal.ZERO;

        Order order = orderMapper.toEntity(orderRequestDTO);
        order.setOrderStatus(OrderStatus.CREATED);

        List<Long> menuItemsId = order.getOrderItems()
                .stream()
                .map(OrderItem::getMenuItemId)
                .toList();

        List<RestaurantMenuItemDTO> fetchedItems = restaurantClient.getItems(menuItemsId);

        for (OrderItem item : order.getOrderItems()) {
            RestaurantMenuItemDTO fetchedItemForOrderItem = fetchedItems
                    .stream()
                    .filter(f -> f.getId().equals(item.getMenuItemId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Meal with id " + item.getMenuItemId() + " not found"));

            item.setName(fetchedItemForOrderItem.getName());
            item.setPrice(fetchedItemForOrderItem.getPrice());

            BigDecimal countPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            itemsTotalPrice = itemsTotalPrice.add(countPrice);


        }

        order.setUserId(getCurrentUserId());
        order.setPickupAddress("Restaurant Address");
        order.setItemsPrice(itemsTotalPrice);
        order.setDeliveryPrice(DELIVERY_PRICE);
        order.setTotalPrice(itemsTotalPrice.add(DELIVERY_PRICE));

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponseDTO(savedOrder);
    }



}
