package com.august.order.client;

import com.august.order.config.FeignConfig;
import com.august.order.dto.RestaurantMenuItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "restaurantholder", url = "${app.services.restaurant-holder.url}", configuration = FeignConfig.class)
public interface RestaurantClient {
    @GetMapping("/api/v1/items")
    List<RestaurantMenuItemDTO> getItems(@RequestParam("ids") List<Long> ids);
}
