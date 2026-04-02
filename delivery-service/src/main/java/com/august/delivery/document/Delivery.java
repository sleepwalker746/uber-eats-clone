package com.august.delivery.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "deliveries")
public class Delivery {

    @Id
    private String id;

    private Long orderId;
    private Long restaurantId;
    private Long customerId;

    private String courierId;

    private DeliveryStatus status;

    private Address address;
    private Address deliveryAddress;

    private Instant createdAt;
    private Instant pickedUpAt;
    private Instant deliveredAt;

}
