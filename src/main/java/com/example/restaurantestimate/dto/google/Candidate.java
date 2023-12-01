package com.example.restaurantestimate.dto.google;

import com.example.restaurantestimate.dto.restaurant.RestaurantReview;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Candidate {
    private long id;
    private String name;
    private String formatted_address;
    private Double rating;
    private OpeningHours opening_hours;
    private Geometry geometry;
    private Boolean openNow;
    private List<RestaurantReview> reviews;
    private String placeId;
    private String photoUrl;
    private boolean servesBeer;
    private boolean servesWine;
}