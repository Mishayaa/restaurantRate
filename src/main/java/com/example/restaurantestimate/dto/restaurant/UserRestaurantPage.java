package com.example.restaurantestimate.dto.restaurant;


import com.example.restaurantestimate.dto.PageDto;
import com.example.restaurantestimate.dto.user.UserDtoResponse;
import com.example.restaurantestimate.dto.user.UserRestaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRestaurantPage extends PageDto {
    private UserDtoResponse user;
    private List<UserRestaurant> restaurants;
}