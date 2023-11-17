package com.example.restaurantestimate.mappers;

import com.example.restaurantestimate.dto.CuisineDto;
import com.example.restaurantestimate.dto.restaurant.RestaurantDto;
import com.example.restaurantestimate.dto.restaurant.RestaurantReview;
import com.example.restaurantestimate.dto.review.ReviewDto;
import com.example.restaurantestimate.entities.Restaurant;
import com.example.restaurantestimate.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RestaurantDtoSerializer implements Function<Restaurant, RestaurantDto> {

    private final ReviewService reviewService;

    @Override
    public RestaurantDto apply(Restaurant restaurant) {
        return RestaurantDto.builder()
                .id(restaurant.getId())
                .posterUrl(restaurant.getPosterUrl())
                .name(restaurant.getName())
                .rating(restaurant.getRating())
                .reviews(
                        reviewService.getRandomReviewsByRestaurantName(restaurant.getName()).stream()
                                .map(e -> new ReviewDto(
                                                e.getId(),
                                                e.getUser(),
                                                e.getRestaurantId(),
                                                e.getReview(),
                                                e.getRating()
                                        )
                                ).toList()
                )
                .build();
    }
}