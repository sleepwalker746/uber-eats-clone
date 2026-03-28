package com.august.order.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "restaurantholder", url = "")
public interface RestaurantClient {
}
