package com.example.restaurantestimate.mappers;


import com.example.restaurantestimate.dto.restaurant.RestaurantDtoShort;
import com.example.restaurantestimate.entities.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class RestaurantDtoShortSerializer implements Function<Restaurant, RestaurantDtoShort> {
    @Override
    public RestaurantDtoShort apply(Restaurant restaurant) {
        return RestaurantDtoShort.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .posterUrl(restaurant.getPosterUrl())
                .build();
    }
}