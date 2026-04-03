package com.august.delivery.controller;

import com.august.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/orders/{orderId}/assign")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<Void> assignCourier(@PathVariable("orderId") Long orderId) {
        deliveryService.assignCourier(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/complete")
    @PreAuthorize("hasRole('COURIER')")
    public ResponseEntity<Void> completeDelivery(@PathVariable("orderId") Long orderId) {
        deliveryService.completeDelivery(orderId);
        return ResponseEntity.ok().build();
    }


}
