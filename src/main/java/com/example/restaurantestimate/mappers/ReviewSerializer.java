package com.example.restaurantestimate.mappers;

import com.example.restaurantestimate.dto.review.ReviewDto;
import com.example.restaurantestimate.entities.Review;
import com.example.restaurantestimate.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ReviewSerializer implements Function<Review, ReviewDto> {
    private final UserService userService;
    @Override
    public ReviewDto apply(Review review) {

        return ReviewDto.builder()
                .id(review.getId())
                .user(userService.getUserById(review.getUser().getId()))
                .restaurantId(review.getRestaurant().getId())
                .review(review.getUserReview())
                .rating(review.getRating())
                .build();
    }
}
