package com.august.delivery.document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {
    private String address;
    private Double latitude;
    private Double longitude;
}
