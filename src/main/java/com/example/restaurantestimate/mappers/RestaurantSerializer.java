package com.example.restaurantestimate.mappers;

import java.util.function.Function;
import java.util.stream.Collectors;

import com.example.restaurantestimate.dto.restaurant.RestaurantDto;
import com.example.restaurantestimate.entities.Restaurant;
import com.example.restaurantestimate.services.CuisineService;
import com.example.restaurantestimate.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantSerializer implements Function<RestaurantDto, Restaurant> {
    private final ReviewService reviewService;

    @Override
    public Restaurant apply(RestaurantDto restaurantDto) {
        return Restaurant.builder()

                .name(restaurantDto.getName())
                .rating(restaurantDto.getRating())
                .posterUrl(restaurantDto.getPosterUrl())
                .formatted_address(restaurantDto.getFormatted_address())
                .open_now(restaurantDto.getOpenNow()).build();

    }
}