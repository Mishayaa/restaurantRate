package com.example.restaurantestimate.dto.user;

import lombok.Data;

@Data
public class UserDtoLoginRequest {
    private String username;
    private String password;
}
