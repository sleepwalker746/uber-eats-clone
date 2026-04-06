package com.august.delivery.service.implementation;

import com.august.common.event.OrderDeliveredEvent;
import com.august.delivery.document.Delivery;
import com.august.delivery.document.DeliveryStatus;
import com.august.delivery.repository.DeliveryRepository;
import com.august.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository  deliveryRepository;
    private final RabbitTemplate rabbitTemplate;

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
}
