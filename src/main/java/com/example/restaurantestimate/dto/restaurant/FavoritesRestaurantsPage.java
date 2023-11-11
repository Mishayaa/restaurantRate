package com.example.restaurantestimate.dto.restaurant;

import com.example.restaurantestimate.dto.PageDto;
import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoritesRestaurantsPage extends PageDto {
    private List<RestaurantCard> restaurants;
}