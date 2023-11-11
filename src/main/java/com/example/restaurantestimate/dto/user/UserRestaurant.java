package com.example.restaurantestimate.dto.user;

import com.example.restaurantestimate.dto.CuisineDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRestaurant {
    private Long id;
    private String name;
    private String description;
    private Double averageRating;
    private Set<CuisineDto> cuisines;
    private String posterUrl;
    private Double rating;
    private String review;
}
