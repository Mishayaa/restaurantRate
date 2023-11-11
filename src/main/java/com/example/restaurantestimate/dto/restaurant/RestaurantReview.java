package com.example.restaurantestimate.dto.restaurant;
import com.example.restaurantestimate.dto.user.UserDtoResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantReview {
    private Long id;
    private UserDtoResponse user;
    private Double rating;
    private String review;
}