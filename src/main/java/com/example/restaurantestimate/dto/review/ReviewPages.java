package com.example.restaurantestimate.dto.review;

import com.example.restaurantestimate.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReviewPages extends PageDto {
    private List<ReviewDto> reviews;
}
