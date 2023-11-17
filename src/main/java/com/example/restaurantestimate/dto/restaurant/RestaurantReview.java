package com.example.restaurantestimate.dto.restaurant;
import com.example.restaurantestimate.dto.user.UserDtoResponse;
import com.example.restaurantestimate.entities.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantReview {
    private Long id;
    private Long restaurantId;
    private UserDtoResponse user;
    private Double rating;
    private Review review;
}