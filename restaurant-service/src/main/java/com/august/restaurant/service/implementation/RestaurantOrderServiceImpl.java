package com.august.restaurant.service.implementation;

import com.august.common.event.OrderCreatedEvent;
import com.august.common.event.OrderReadyEvent;
import com.august.restaurant.entity.OrderStatus;
import com.august.restaurant.entity.Restaurant;
import com.august.restaurant.entity.RestaurantOrder;
import com.august.restaurant.repository.RestaurantOrderRepository;
import com.august.restaurant.repository.RestaurantRepository;
import com.august.restaurant.service.interfaces.RestaurantOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantOrderServiceImpl implements RestaurantOrderService {

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantRepository restaurantRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void markOrderAsReady(Long orderId) {

        RestaurantOrder order = restaurantOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Restaurant Order Not Found"));

        if (order.getOrderStatus() != OrderStatus.PREPARING) {
            throw new IllegalStateException("Order Not Prepared");
        }

        order.setOrderStatus(OrderStatus.READY);
        restaurantOrderRepository.save(order);

        log.info("Restaurant Order №{} Marked as Ready", orderId);

        rabbitTemplate.convertAndSend("restaurant.exchange", "restaurant.order.ready", new OrderReadyEvent(orderId, order.getRestaurant().getId()));

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
