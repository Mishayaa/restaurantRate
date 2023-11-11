package com.example.restaurantestimate.mappers;

import com.example.restaurantestimate.dto.CuisineDto;
import com.example.restaurantestimate.dto.restaurant.RestaurantDto;
import com.example.restaurantestimate.dto.restaurant.RestaurantReview;
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
                .description(restaurant.getDescription())
                .posterUrl(restaurant.getPosterUrl())
                .name(restaurant.getName())
                .averageRating(reviewService.getAverageReviewRatingById(restaurant.getId()))
                .cuisines(restaurant.getCuisines().stream()
                        .map(e -> new CuisineDto(e.getName()))
                        .collect(Collectors.toSet()))
                .rating(restaurant.getRating())
                .reviews(
                        reviewService.getRandomReviewsByRestaurantId(restaurant.getId()).stream()
                                .map(e -> new RestaurantReview(
                                        e.getId(),
                                        e.getUser(),
                                        e.getRating(),
                                        e.getReview())
                                ).toList()
                )
                .build();
    }
}