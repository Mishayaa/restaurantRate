package com.example.restaurantestimate.mappers;

import java.util.function.Function;
import java.util.stream.Collectors;

import com.example.restaurantestimate.dto.restaurant.RestaurantDto;
import com.example.restaurantestimate.entities.Restaurant;
import com.example.restaurantestimate.services.CuisineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantSerializer implements Function<RestaurantDto, Restaurant> {
    private final CuisineService cuisineService;

    @Override
    public Restaurant apply(RestaurantDto restaurantDto) {
        return Restaurant.builder()
                .id(restaurantDto.getId())
                .description(restaurantDto.getDescription())
                .name(restaurantDto.getName())
                .rating(restaurantDto.getRating())
                .posterUrl(restaurantDto.getPosterUrl())
                .cuisines(restaurantDto.getCuisines().stream()
                        .map(cuisine -> cuisineService.findCuisine(cuisine.getName()))
                        .collect(Collectors.toSet()))
                .build();
    }
}