package com.august.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "Login cannot be empty!")
    private String login;
    @NotBlank(message = "Password cannot be empty!")
    private String password;
}
