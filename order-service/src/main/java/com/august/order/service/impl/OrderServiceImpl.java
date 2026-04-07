package com.august.order.service.impl;

import com.august.common.event.OrderCreatedEvent;
import com.august.common.event.OrderDeliveredEvent;
import com.august.common.event.OrderDeliveringEvent;
import com.august.order.client.RestaurantClient;
import com.august.order.dto.OrderRequestDTO;
import com.august.order.dto.OrderResponseDTO;
import com.august.order.dto.RestaurantDTO;
import com.august.order.dto.RestaurantMenuItemDTO;
import com.august.order.entity.Order;
import com.august.order.entity.OrderItem;
import com.august.order.entity.OrderStatus;
import com.august.order.mapper.OrderMapper;
import com.august.order.outbox.OutboxEvent;
import com.august.order.outbox.OutboxRepository;
import com.august.order.outbox.OutboxStatus;
import com.august.order.repository.OrderRepository;
import com.august.order.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {


    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ObjectMapper objectMapper;
    private final OutboxRepository outboxRepository;
    private final RestaurantClient restaurantClient;
    private static final BigDecimal DELIVERY_PRICE = BigDecimal.valueOf(1);

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
            Object userIdClaim = jwt.getClaim("userId");

            if (userIdClaim != null) {
                return Long.valueOf(userIdClaim.toString());
            }
        }
        throw new RuntimeException("Couldn't find the userId in the token. Please log in again!");
    }

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {

        BigDecimal itemsTotalPrice = BigDecimal.ZERO;

        Order order = orderMapper.toEntity(orderRequestDTO);
        order.setOrderStatus(OrderStatus.CREATED);

        RestaurantDTO restaurantDTO = restaurantClient.getRestaurantById(orderRequestDTO.getRestaurantId());
        order.setPickupAddress(restaurantDTO.getAddress());

        List<Long> menuItemsId = order.getOrderItems()
                .stream()
                .map(OrderItem::getMenuItemId)
                .toList();

        List<RestaurantMenuItemDTO> fetchedItems = restaurantClient.getItems(menuItemsId);

        Map<Long, RestaurantMenuItemDTO> fetchedMenuItems = fetchedItems
                .stream()
                .collect(Collectors.toMap(RestaurantMenuItemDTO::getId, i -> i));

        for (OrderItem orderItem : order.getOrderItems()) {
            RestaurantMenuItemDTO fetchedItem = fetchedMenuItems.get(orderItem.getMenuItemId());

            if (fetchedItem == null) {
                throw new RuntimeException("Dish with id " + orderItem.getMenuItemId() + " not found in the restaurant's response!");
            }

            orderItem.setName(fetchedItem.getName());
            orderItem.setPrice(fetchedItem.getPrice());

            BigDecimal countPrice = orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            itemsTotalPrice = itemsTotalPrice.add(countPrice);
        }

        order.setUserId(getCurrentUserId());
        order.setItemsPrice(itemsTotalPrice);
        order.setDeliveryPrice(DELIVERY_PRICE);
        order.setTotalPrice(itemsTotalPrice.add(DELIVERY_PRICE));

        Order savedOrder = orderRepository.save(order);

        try {
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType("Order")
                    .aggregateId(savedOrder.getId().toString())
                    .type("OrderCreatedEvent")
                    .payload(objectMapper.writeValueAsString(
                            new OrderCreatedEvent(
                                    savedOrder.getId(),
                                    savedOrder.getUserId(),
                                    savedOrder.getRestaurantId(),
                                    savedOrder.getTotalPrice()
                            )
                    ))
                    .status(OutboxStatus.NEW)
                    .retryCount(0)
                    .createdAt(Instant.now())
                    .build();

            outboxRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize OrderCreatedEvent", e);
        }

        return orderMapper.toResponseDTO(savedOrder);
    }

    @Override
    @Transactional
    public void handleOrderDeliveringEvent(OrderDeliveringEvent event) {

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Order with id: " + event.orderId() + "not found!"));

        if(OrderStatus.DELIVERING.equals(order.getOrderStatus())) {
            return;
        }

        if(OrderStatus.CANCELED.equals(order.getOrderStatus())) {
            throw new IllegalArgumentException("Order with id: " + event.orderId() + " has been cancelled!");
        }

        order.setOrderStatus(OrderStatus.DELIVERING);
        orderRepository.save(order);

    }

    @Override
    @Transactional
    public void handleOrderDeliveredEvent(OrderDeliveredEvent event) {

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Order with id: " + event.orderId() + "not found!"));

        if(OrderStatus.DELIVERED.equals(order.getOrderStatus())) {
            return;
        }

        if(OrderStatus.CANCELED.equals(order.getOrderStatus())) {
            throw new IllegalArgumentException("Order with id: " + event.orderId() + " has been cancelled!");
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }


}
