package com.example.restaurantestimate.dto.user;

import lombok.Data;

@Data
public class UserRegistrationDtoRequest {
    private String username;
    private String email;
    private String password;


}