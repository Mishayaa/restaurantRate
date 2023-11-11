package com.example.restaurantestimate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ReviewDtoRequest {
    private Long restaurantId;
    private String review;
    private Double rating;
}