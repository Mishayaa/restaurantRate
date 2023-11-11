package com.example.restaurantestimate.dto.restaurant;

import com.example.restaurantestimate.dto.CuisineDto;
import com.example.restaurantestimate.dto.PageDto;
import com.example.restaurantestimate.entities.Cuisine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto extends PageDto {
    private Long id;
    private String name;
    private String description;
    private Double rating;
    private Set<CuisineDto> cuisines;
    private Double averageRating;
    private String posterUrl;
    private List<RestaurantReview> reviews;
}