package com.example.restaurantestimate.dto.review;

import com.example.restaurantestimate.dto.user.UserDtoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReviewDto {
    private Long id;
    private UserDtoResponse user;
    private Long restaurantId;
    private String review;
    private Double rating;
}