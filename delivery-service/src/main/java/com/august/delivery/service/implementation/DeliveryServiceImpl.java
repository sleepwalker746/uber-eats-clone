package com.august.delivery.service.implementation;

import com.august.common.event.*;
import com.august.delivery.document.Delivery;
import com.august.delivery.document.DeliveryStatus;
import com.august.delivery.mapper.DeliveryMapper;
import com.august.delivery.repository.DeliveryRepository;
import com.august.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository  deliveryRepository;
    private final RabbitTemplate rabbitTemplate;
    private final DeliveryMapper deliveryMapper;

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
    public void assignCourier(Long orderId) {

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found!"));

        if(delivery.getStatus() != DeliveryStatus.SEARCHING_COURIER) {
            throw new IllegalStateException("Delivery status is not SEARCHING_COURIER");
        }

        delivery.setStatus(DeliveryStatus.COURIER_ASSIGNED);
        delivery.setCourierId(getCurrentUserId());

        deliveryRepository.save(delivery);

        log.info("Courier with id {} has been assigned to order with id {}", delivery.getCourierId(), orderId);

    }

    @Override
    public void completeDelivery(Long orderId) {

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found!"));
        if (!delivery.getCourierId().equals(getCurrentUserId())) {
            throw new RuntimeException("Access Denied: You cannot complete someone else's order!");
        }

        if (delivery.getStatus() != DeliveryStatus.DELIVERING) {
            throw new IllegalStateException("Delivery cannot be completed. Current status: " + delivery.getStatus());
        }

        delivery.setStatus(DeliveryStatus.DELIVERED);
        deliveryRepository.save(delivery);

        log.info("Delivery has been completed for order with id {}", orderId);

        rabbitTemplate.convertAndSend("delivery.exchange", "delivery.order.delivered", new OrderDeliveredEvent(orderId));
    }

    @Override
    @Transactional
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

    @Retryable(
            retryFor = RuntimeException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay =  500)
    )
    @Override
    @Transactional
    public void handleOrderPreparingEvent(OrderPreparingEvent event) {

        log.info("Restaurant has started preparing order №{}. Looking for courier...", event.orderId());

        Delivery delivery = deliveryRepository.findByOrderId(event.orderId())
                .orElseThrow(() -> new RuntimeException("Delivery Not Found for order: " + event.orderId()));

        delivery.setStatus(DeliveryStatus.SEARCHING_COURIER);

        deliveryRepository.save(delivery);

        log.info("Order Delivery Status №{} has been changed on SEARCHING_COURIER", event.orderId());

    }

    @Override
    @Transactional
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
