package com.august.auth.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Set;

@Data
public class AccountResponseDTO {
    private Long id;
    private String login;
    private String email;
    private String phone;
    private String name;
    private String surname;
    private Set<String> roles;
    private ZonedDateTime createdAt;
    private String token;
}
