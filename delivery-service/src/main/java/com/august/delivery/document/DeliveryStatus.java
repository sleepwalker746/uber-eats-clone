package com.august.delivery.document;

import lombok.Getter;

@Getter
public enum DeliveryStatus {
    WAITING_FOR_PREPARATION,
    SEARCHING_COURIER,
    COURIER_ASSIGNED,
    DELIVERING,
    DELIVERED
}
