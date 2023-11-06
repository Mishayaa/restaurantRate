package com.example.restaurantestimate.dto;

import lombok.Data;

@Data
public class UserRegistrationDtoRequest {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

}