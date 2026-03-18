package com.august.restaurant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantRequestDTO {

    @Size(max=255, message = "Name cannot be longer than 255 characters!")
    @NotBlank(message = "Name of a restaurant cannot be empty!")
    String name;

    String address;

    @Size(max=50, message = "Phone number cannot be longer than 50 characters!")
    String phone;

    @Size(max = 225, message = "Email cannot be longer than 255 characters!")
    @Email
    String email;

    @URL(message = "Invalid URL!")
    String imageUrl;

}
