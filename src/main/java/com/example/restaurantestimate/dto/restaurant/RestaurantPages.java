package com.example.restaurantestimate.dto.restaurant;

import com.example.restaurantestimate.dto.PageDto;
import com.example.restaurantestimate.dto.restaurant.RestaurantDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantPages extends PageDto {
    private List<RestaurantDto> restaurants;
}
