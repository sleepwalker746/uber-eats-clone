package com.august.delivery.mapper;

import com.august.delivery.document.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DeliveryMapper {
    @Named("stringToAddress")
    default Address stringToAddress(String address) {
        if (address == null) {
            return null;
        }
        // TODO: implement real geocoding
        return Address.builder()
                .address(address)
                .latitude(null)
                .longitude(null)
                .build();
    }
}
