package com.example.restaurantestimate.services;


import com.example.restaurantestimate.dto.restaurant.RestaurantPages;
import com.example.restaurantestimate.entities.Restaurant;
import com.example.restaurantestimate.mappers.GooglePlacesApiSerializer;
import com.example.restaurantestimate.mappers.PageMapper;
import com.example.restaurantestimate.mappers.RestaurantSerializer;
import com.example.restaurantestimate.repositories.RestaurantCustomRepositoryImpl;
import com.example.restaurantestimate.repositories.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleRestaurantService {
    private final GoogleExternalApiService externalApiService;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantSerializer restaurantSerializer;
    private final RestaurantCustomRepositoryImpl restaurantCustomRepository;
    private final PageMapper pageMapper;


    public RestaurantPages getRestaurantByName(String name, Integer page, Integer limit) {

        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Restaurant> restaurantPage = restaurantCustomRepository.searchBy(name, pageRequest, "name");
        if (restaurantPage == null || restaurantPage.isEmpty()) {
            try {
                RestaurantPages tripRestaurant = null;
                try {
                    tripRestaurant = externalApiService.findRestaurantByName(name, page, limit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                List<Restaurant> restaurants = tripRestaurant.getRestaurants().stream()
                        .filter(e -> restaurantRepository.findByName(e.getName()).isEmpty())
                        .map(restaurantSerializer)
                        .toList();

                try {
                    restaurantRepository.saveAll(restaurants);
                } catch (DataIntegrityViolationException e) {
                    e.getMessage();
                }


                return tripRestaurant;
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.error("Ресурс не найден: " + e.getStatusText());
                }
            }
        }
        return pageMapper.buildRestaurantPage(limit, page, restaurantPage);

    }




}