package com.august.restaurant.service.implementation;

import com.august.common.event.OrderPreparingEvent;
import com.august.common.event.PaymentCompletedEvent;
import com.august.common.event.PaymentFailedEvent;
import com.august.restaurant.entity.OrderStatus;
import com.august.restaurant.entity.Restaurant;
import com.august.restaurant.entity.RestaurantOrder;
import com.august.restaurant.repository.RestaurantOrderRepository;
import com.august.restaurant.repository.RestaurantRepository;
import com.august.restaurant.service.interfaces.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantRepository restaurantRepository;
    private final RabbitTemplate rabbitTemplate;

    private RestaurantOrder getOrCreateRestaurantOrder(Long orderId, Long restaurantId) {
        return restaurantOrderRepository.findByOrderId(orderId)
                .orElseGet(() -> {
                    RestaurantOrder newOrder = new RestaurantOrder();
                    newOrder.setOrderId(orderId);

                    Restaurant restaurant = restaurantRepository.findById(restaurantId)
                            .orElseThrow(() -> new RuntimeException("Restaurant Not Found"));

                    newOrder.setRestaurant(restaurant);
                    return newOrder;
                });
    }

    private void updateOrderStatus(Long orderId, Long restaurantId, OrderStatus status) {
        RestaurantOrder restaurantOrder = getOrCreateRestaurantOrder(orderId, restaurantId);
        restaurantOrder.setOrderStatus(status);
        restaurantOrderRepository.save(restaurantOrder);
    }



    @Override
    @Transactional
    public void handlePaymentCompletedEvent(PaymentCompletedEvent event) {

        updateOrderStatus(
                event.orderId(),
                event.restaurantId(),
                OrderStatus.PREPARING
        );

        rabbitTemplate.convertAndSend(
                "restaurant.exchange",
                "restaurant.order.preparing",
                new OrderPreparingEvent(event.orderId(), event.restaurantId())
        );

    }

    @Override
    @Transactional
    public void handlePaymentFailedEvent(PaymentFailedEvent event) {

        updateOrderStatus(
                event.orderId(),
                event.restaurantId(),
                OrderStatus.CANCELED
        );

    }
}
