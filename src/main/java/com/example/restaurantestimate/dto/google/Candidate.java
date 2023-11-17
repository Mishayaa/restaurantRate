package com.example.restaurantestimate.dto.google;

import com.example.restaurantestimate.dto.CuisineDto;
import com.example.restaurantestimate.dto.restaurant.RestaurantReview;
import com.example.restaurantestimate.entities.Review;
import com.example.restaurantestimate.pojo_tripadvisor.Photo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Candidate {
    private String name;
    private String formatted_address;
    private Double rating;
    private OpeningHours opening_hours;
    private Geometry geometry;
    private Boolean openNow;
    private List<RestaurantReview> reviews;
    private String placeId;
    private String photoUrl;
}