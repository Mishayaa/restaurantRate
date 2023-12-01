package com.example.restaurantestimate.mappers;

import com.example.restaurantestimate.dto.restaurant.RestaurantDto;
import com.example.restaurantestimate.dto.restaurant.RestaurantDtoShort;
import com.example.restaurantestimate.dto.restaurant.RestaurantPages;
import com.example.restaurantestimate.dto.restaurant.RestaurantPagesShort;
import com.example.restaurantestimate.entities.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
@RequiredArgsConstructor
public class PageMapper {
    private final RestaurantDtoSerializer restaurantDtoSerializer;
    private final RestaurantDtoShortSerializer restaurantDtoShortSerializer;

    public RestaurantPages buildRestaurantPage(Integer limit, Integer page, Page<Restaurant> restaurantPage) {
        List<RestaurantDto> restaurants = restaurantPage.getContent().stream()
                .map(restaurantDtoSerializer)
                .toList();
        RestaurantPages restaurantPages = new RestaurantPages(restaurants);
        restaurantPages.setPage(page);
        restaurantPages.setPages(restaurantPage.getTotalPages());
        restaurantPages.setTotal(restaurantPage.getTotalElements());
        restaurantPages.setLimit(limit);

        return restaurantPages;
    }

   public RestaurantPagesShort buildMoviePageShortFromKp(Integer limit, Integer page,
                                                     RestaurantPages moviePage, List<Restaurant> movies) {
        List<RestaurantDtoShort> movieDtoShorts = movies.stream()
                .map(restaurantDtoShortSerializer)
                .toList();

       RestaurantPagesShort moviePagesShort = new RestaurantPagesShort(movieDtoShorts);
        moviePagesShort.setPage(page);
        moviePagesShort.setPages(moviePage.getPages());
        moviePagesShort.setTotal(moviePage.getTotal());
        moviePagesShort.setLimit(limit);

        return moviePagesShort;
    }

    public RestaurantPagesShort buildRestaurantPageShort(Integer limit, Integer page, Page<Restaurant> restaurantPage) {
        List<RestaurantDtoShort> restaurants = restaurantPage.getContent().stream()
                .map(restaurantDtoShortSerializer)
                .toList();

        RestaurantPagesShort restaurantPagesShort = new RestaurantPagesShort(restaurants);
        restaurantPagesShort.setPage(page);
        restaurantPagesShort.setPages(restaurantPage.getTotalPages());
        restaurantPagesShort.setTotal(restaurantPage.getTotalElements());
        restaurantPagesShort.setLimit(limit);

        return restaurantPagesShort;
    }
}