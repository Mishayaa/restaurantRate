package com.example.restaurantestimate.dto.restaurant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantCard {
    private Long id;
    private String name;
    private String description;
    private Double averageRating;
    private String posterUrl;
    private Boolean isFavorite;
    private boolean hasBeer;
    private boolean hasWine;
    private boolean hasSnacks;
    private boolean hasCocktails;
    private boolean hasTerrace;
    private boolean hasTakeaway;
}