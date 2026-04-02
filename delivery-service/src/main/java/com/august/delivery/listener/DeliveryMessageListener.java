package com.august.delivery.listener;

import com.august.common.event.OrderPaidEvent;
import com.august.delivery.document.Delivery;
import com.august.delivery.document.DeliveryStatus;
import com.august.delivery.mapper.DeliveryMapper;
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
    private final DeliveryMapper deliveryMapper;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "delivery.order.paid.queue", durable = "true"),
            exchange = @Exchange(value = "order.exchange", type = ExchangeTypes.TOPIC),
            key = "order.paid"
    ))
    public void handleOrderPaidEvent(OrderPaidEvent event) {
        log.info("Got an event,  №{}", event.orderId());
        Delivery delivery = Delivery.builder()
                .orderId(event.orderId())
                .status(DeliveryStatus.WAITING_FOR_PREPARATION)
                .restaurantId(event.restaurantId())
                .deliveryAddress(deliveryMapper.stringToAddress(event.deliveryAddress()))
                .createdAt(Instant.now())
                .build();
        deliveryRepository.save(delivery);
        log.info("Delivery initialized and waiting for kitchen for order №{}", event.orderId());
    }
}
