package com.august.restaurant.service.implementation;

import com.august.common.event.OrderCreatedEvent;
import com.august.common.event.OrderReadyEvent;
import com.august.restaurant.entity.OrderStatus;
import com.august.restaurant.entity.Restaurant;
import com.august.restaurant.entity.RestaurantOrder;
import com.august.restaurant.outbox.OutboxEvent;
import com.august.restaurant.outbox.OutboxRepository;
import com.august.restaurant.outbox.OutboxStatus;
import com.august.restaurant.repository.RestaurantOrderRepository;
import com.august.restaurant.repository.RestaurantRepository;
import com.august.restaurant.service.interfaces.RestaurantOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantOrderServiceImpl implements RestaurantOrderService {

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantRepository restaurantRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    private OutboxEvent buildOutboxEvent(String aggregateType,
                                         String aggregateId,
                                         Object payload) {
        try {
            return OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .payload(objectMapper.writeValueAsString(payload))
                    .status(OutboxStatus.NEW)
                    .retryCount(0)
                    .createdAt(Instant.now())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event: ", e);
        }
    }


    @Override
    @Transactional
    public void markOrderAsReady(Long orderId) {

        RestaurantOrder order = restaurantOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Restaurant Order Not Found"));

        if (order.getOrderStatus() != OrderStatus.PREPARING) {
            throw new IllegalStateException("Order Not Prepared");
        }

        order.setOrderStatus(OrderStatus.READY);
        restaurantOrderRepository.save(order);

        log.info("Restaurant Order №{} Marked as Ready", orderId);

        OutboxEvent outboxEvent = buildOutboxEvent(
                "RestaurantOrder",
                orderId.toString(),
                new OrderReadyEvent(orderId, order.getRestaurant().getId())
        );

        outboxRepository.save(outboxEvent);

    }

    @Override
    @Transactional
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {

        log.info("Received Order Created Event");

        if (restaurantOrderRepository.existsByOrderId(event.orderId())) {
            return;
        }

        try {

            RestaurantOrder restaurantOrder = new RestaurantOrder();
            restaurantOrder.setOrderId(event.orderId());

            Restaurant restaurant = restaurantRepository.findById(event.restaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));

            restaurantOrder.setRestaurant(restaurant);
            restaurantOrder.setOrderStatus(OrderStatus.WAITING_FOR_PAYMENT);

            restaurantOrderRepository.save(restaurantOrder);
            log.info("Order Created Successfully");

        } catch (DataIntegrityViolationException e) {

            log.info("Race conditions violated");

        }
    }
}
