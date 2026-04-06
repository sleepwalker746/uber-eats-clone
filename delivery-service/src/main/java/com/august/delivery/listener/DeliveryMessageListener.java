package com.august.delivery.listener;

import com.august.common.event.*;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryMessageListener {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final RabbitTemplate rabbitTemplate;

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

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "delivery.order.preparing.queue", durable = "true"),
            exchange = @Exchange(value = "restaurant.exchange", type = ExchangeTypes.TOPIC),
            key = "restaurant.order.preparing"
    ))
    public void handleOrderPreparing(OrderPreparingEvent event) {
        log.info("Restaurant has started preparing order №{}. Looking for courier...", event.orderId());

        Delivery delivery = deliveryRepository.findByOrderId(event.orderId())
                .orElseThrow(() -> new RuntimeException("Delivery Not Found for order: " + event.orderId()));

        delivery.setStatus(DeliveryStatus.SEARCHING_COURIER);

        deliveryRepository.save(delivery);

        log.info("Order Delivery Status №{} has been changed on SEARCHING_COURIER", event.orderId());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "delivery.order.ready.queue", durable = "true"),
            exchange = @Exchange(value = "restaurant.exchange", type = ExchangeTypes.TOPIC),
            key = "restaurant.order.ready"

    ))
    public void handleOrderReadyEvent(OrderReadyEvent event) {

        log.info("Restaurant has prepared an order №{}. Hanging to courier...", event.orderId());

        Delivery delivery = deliveryRepository.findByOrderId(event.orderId())
                .orElseThrow(() -> new RuntimeException("Delivery Not Found for order: " + event.orderId()));

        if(delivery.getStatus() != DeliveryStatus.COURIER_ASSIGNED) {
            log.warn("Warning! Order №{} is ready, but order status is {} ", event.orderId(), delivery.getStatus());
        }

        delivery.setStatus(DeliveryStatus.DELIVERING);
        delivery.setPickedUpAt(Instant.now());

        deliveryRepository.save(delivery);

        log.info("Courier №{} took an order and on it's way to the customer", delivery.getCourierId());

        rabbitTemplate.convertAndSend("delivery.exchange", "delivery.order.delivering", new OrderDeliveringEvent(event.orderId()));

    }









}
