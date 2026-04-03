package com.august.restaurant.listener;

import com.august.common.event.OrderPreparingEvent;
import com.august.common.event.PaymentCompletedEvent;
import com.august.common.event.PaymentFailedEvent;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

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

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "restaurant.payment.completed.queue", durable = "true"),
            exchange = @Exchange(value = "payment.exchange", type = ExchangeTypes.TOPIC),
            key = "payment.completed"
    ))
    public void handlePaymentConfirmed(PaymentCompletedEvent event) {
        log.info("Received PaymentCompletedEvent");

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

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "restaurant.payment.failed.queue", durable = "true"),
            exchange = @Exchange(value = "payment.exchange", type = ExchangeTypes.TOPIC),
            key = "payment.failed"
    ))
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("Received PaymentFailedEvent");

        updateOrderStatus(
                event.orderId(),
                event.restaurantId(),
                OrderStatus.CANCELED
        );

        log.info("Saga refund has been processed: Status order №{} has been changed on CANCELED", event.orderId());
    }

}
