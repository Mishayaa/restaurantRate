package com.example.restaurantestimate.mappers;

import com.example.restaurantestimate.dto.restaurant.RestaurantCard;
import com.example.restaurantestimate.dto.review.ReviewDto;
import com.example.restaurantestimate.dto.user.UserRestaurant;
import com.example.restaurantestimate.entities.Restaurant;
import com.example.restaurantestimate.services.ReviewService;
import com.example.restaurantestimate.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantMapper {
    private final ReviewService reviewService;
    private final UserService userService;

    public UserRestaurant toUserRestaurantDto(Restaurant restaurant, Long userId) {
        ReviewDto reviewByUser = reviewService.getReviewByUserIdAndMovieId(userId, restaurant.getId());
        return UserRestaurant.builder()
                .id(restaurant.getId())
                .posterUrl(restaurant.getPosterUrl())
                .name(restaurant.getName())
                .averageRating(reviewService.getAverageReviewRatingById(restaurant.getId()))
                .formatted_address(restaurant.getFormatted_address())
                .review(reviewByUser.getReview())
                .rating(reviewByUser.getRating())

                .build();
    }

    public RestaurantCard toRestaurantCard(Restaurant restaurant, Long userId) {
        return RestaurantCard.builder()
                .id(restaurant.getId())
                .posterUrl(restaurant.getPosterUrl())
                .name(restaurant.getName())
                .hasBeer(restaurant.isServesBeer())
                .formatted_address(restaurant.getFormatted_address())
                .averageRating(reviewService.getAverageReviewRatingById(restaurant.getId()))

                .isFavorite(userService.isFavoriteMovieForCurrentUser(userId, restaurant.getId()))
                .build();
    }
    public RestaurantCard toRestaurantCard(Restaurant restaurant) {
        return RestaurantCard.builder()
                .id(restaurant.getId())
                .posterUrl(restaurant.getPosterUrl())
                .name(restaurant.getName())
                .hasBeer(restaurant.isServesBeer())
                .formatted_address(restaurant.getFormatted_address())
                .averageRating(reviewService.getAverageReviewRatingById(restaurant.getId()))
                .build();
    }
}
