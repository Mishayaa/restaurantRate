package com.example.restaurantestimate.mappers;

import com.example.restaurantestimate.dto.CuisineDto;
import com.example.restaurantestimate.dto.restaurant.RestaurantCard;
import com.example.restaurantestimate.dto.review.ReviewDto;
import com.example.restaurantestimate.dto.user.UserRestaurant;
import com.example.restaurantestimate.entities.Restaurant;
import com.example.restaurantestimate.services.ReviewService;
import com.example.restaurantestimate.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RestaurantMapper {
    private final ReviewService reviewService;
    private final UserService userService;

    public UserRestaurant toUserRestaurantDto(Restaurant restaurant, Long userId) {
        ReviewDto reviewByUser = reviewService.getReviewByUserIdAndMovieId(userId, restaurant.getId());
        return UserRestaurant.builder()
                .id(restaurant.getId())
                .description(restaurant.getDescription())
                .posterUrl(restaurant.getPosterUrl())
                .name(restaurant.getName())
                .averageRating(reviewService.getAverageReviewRatingById(restaurant.getId()))
                .cuisines(restaurant.getCuisines().stream()
                        .map(e -> new CuisineDto(e.getName()))
                        .collect(Collectors.toSet()))
                .review(reviewByUser.getReview())
                .rating(reviewByUser.getRating())
                .build();
    }

    public RestaurantCard toRestaurantCard(Restaurant restaurant, Long userId) {
        return RestaurantCard.builder()
                .id(restaurant.getId())
                .description(restaurant.getDescription())
                .posterUrl(restaurant.getPosterUrl())
                .name(restaurant.getName())
                .averageRating(reviewService.getAverageReviewRatingById(restaurant.getId()))
                .cuisines(restaurant.getCuisines().stream()
                        .map(e -> new CuisineDto(e.getName()))
                        .collect(Collectors.toSet()))
                .isFavorite(userService.isFavoriteMovieForCurrentUser(userId, restaurant.getId()))
                .build();
    }
}
