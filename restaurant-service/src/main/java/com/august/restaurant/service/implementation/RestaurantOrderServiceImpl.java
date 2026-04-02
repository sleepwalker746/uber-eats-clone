package com.august.restaurant.service.implementation;

import com.august.common.event.OrderReadyEvent;
import com.august.restaurant.entity.OrderStatus;
import com.august.restaurant.entity.RestaurantOrder;
import com.august.restaurant.repository.RestaurantOrderRepository;
import com.august.restaurant.service.interfaces.RestaurantOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantOrderServiceImpl implements RestaurantOrderService {

    private final RestaurantOrderRepository restaurantOrderRepository;
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
}
