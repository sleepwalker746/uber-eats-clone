package com.august.auth.mapper;

import com.august.auth.dto.AccountResponseDTO;
import com.august.auth.dto.RegisterRequestDTO;
import com.august.auth.entity.Account;
import com.august.auth.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    Account toAccount(RegisterRequestDTO registerRequestDTO);
    @Mapping(target = "roles", qualifiedByName = "mapRoles")
    AccountResponseDTO toAccountResponseDTO(Account account, String token);

    @Named("mapRoles")
    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
