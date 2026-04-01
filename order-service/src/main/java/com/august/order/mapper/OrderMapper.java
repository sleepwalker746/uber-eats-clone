package com.august.order.mapper;

import com.august.order.dto.OrderRequestDTO;
import com.august.order.dto.OrderResponseDTO;
import com.august.order.entity.Order;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(source = "orderItemRequestDTOList", target = "orderItems")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderStatus",  ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    Order toEntity(OrderRequestDTO orderRequestDTO);

    @Mapping(source = "orderItems", target = "orderItems")
    OrderResponseDTO toResponseDTO(Order order);

    @AfterMapping
    default void linkOrderItems(@MappingTarget Order order) {
        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(item -> item.setOrder(order));
        }
    }
}
