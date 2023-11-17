package com.example.restaurantestimate.dto.restaurant;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PopularRestaurantDto {

    private Long id;
    private String name;
    private String posterUrl;
}