package com.august.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank(message = "Login cannot be empty!")
    @Size(min = 3, max = 50, message = "Login should be from 3 ot 5 characters")
    private String login;
    @NotBlank(message = "Email is necessary!")
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "Phone number is necessary!")
    private String phone;
    @NotBlank(message = "Password is necessary!")
    @Size(min = 8, message = "Password cannot be shorter than 8 characters!")
    private String password;
    @NotBlank(message = "Confirming password is necessary!")
    private String confirmPassword;
    @NotBlank(message = "Name is necessary!")
    private String name;
    @NotBlank(message = "Surname is necessary!")
    private String surname;
}
