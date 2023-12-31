package com.example.restaurantestimate.dto.google;

import com.example.restaurantestimate.dto.google.Candidate;
import com.example.restaurantestimate.dto.restaurant.RestaurantDto;
import com.example.restaurantestimate.entities.Restaurant;
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
public class GoogleRestaurantInfo {
    private Set<Candidate> candidates;
    private String nextPageToken;

}