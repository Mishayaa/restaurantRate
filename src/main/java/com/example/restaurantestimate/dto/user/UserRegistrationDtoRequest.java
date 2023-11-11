package com.example.restaurantestimate.dto.user;

import lombok.Data;

@Data
public class UserRegistrationDtoRequest {
    private String username;
    private String name;
    private String email;
    private String password;


}