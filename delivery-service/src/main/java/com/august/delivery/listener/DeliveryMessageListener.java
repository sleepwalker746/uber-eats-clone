package com.august.delivery.listener;

import com.august.common.event.OrderPreparingEvent;
import com.august.delivery.document.Delivery;
import com.august.delivery.document.DeliveryStatus;
import com.august.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryMessageListener {
    private final DeliveryRepository deliveryRepository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "delivery.order.preparing.queue", durable = "true"),
            exchange = @Exchange(value = "restaurant.exchange", type = ExchangeTypes.TOPIC),
            key = "restaurant.order.preparing"
    ))
    public void handleOrderPreparingEvent(OrderPreparingEvent event) {
        log.info("Got an event, preparing on order №{}", event.orderId());
        Delivery delivery = Delivery.builder()
                .orderId(event.orderId())
                .status(DeliveryStatus.PENDING)
                .createdAt(Instant.now())
                .build();
        deliveryRepository.save(delivery);
        log.info("Successful delivering an order №{}", event.orderId());
    }
}
