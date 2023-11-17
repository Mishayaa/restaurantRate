package com.example.restaurantestimate.dto.restaurant;

import com.example.restaurantestimate.dto.google.Geometry;
import com.example.restaurantestimate.dto.google.OpeningHours;
import com.example.restaurantestimate.dto.review.ReviewDto;
import com.example.restaurantestimate.entities.Review;
import com.example.restaurantestimate.pojo_tripadvisor.Photo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto {
    private Long id;
    private String placeID;
    private String name;
    private Double rating;
    private String posterUrl;
    private Boolean openNow;
    private Geometry geometry;
    private String formatted_address;
    private OpeningHours opening_hours;
    private List<ReviewDto> reviews;


}