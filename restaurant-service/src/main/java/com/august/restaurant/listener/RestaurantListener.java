package com.august.restaurant.listener;

import com.august.common.event.OrderCreatedEvent;
import com.august.restaurant.entity.OrderStatus;
import com.august.restaurant.entity.Restaurant;
import com.august.restaurant.entity.RestaurantOrder;
import com.august.restaurant.repository.RestaurantOrderRepository;
import com.august.restaurant.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantListener {

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "restaurant.order.created.queue", durable = "true"),
            exchange = @Exchange(value = "order.exchange", type = ExchangeTypes.TOPIC),
            key = "order.created"
    ))
    public void handleOrderCreated(OrderCreatedEvent event) {

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
