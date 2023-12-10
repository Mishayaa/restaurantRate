package com.example.restaurantestimate.services;


import com.example.restaurantestimate.dto.restaurant.RestaurantDto;
import com.example.restaurantestimate.dto.restaurant.RestaurantPages;
import com.example.restaurantestimate.entities.Restaurant;
import com.example.restaurantestimate.mappers.GooglePlacesApiSerializer;
import com.example.restaurantestimate.mappers.PageMapper;
import com.example.restaurantestimate.mappers.RestaurantDtoSerializer;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleRestaurantService {
    private final GoogleExternalApiService externalApiService;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantSerializer restaurantSerializer;
    private final RestaurantDtoSerializer restaurantDtoSerializer;
    private final RestaurantCustomRepositoryImpl restaurantCustomRepository;
    private final PageMapper pageMapper;
    private final HashSet<String> names;

    @Transactional
    public RestaurantPages getRestaurantByName(String name, Integer page, Integer limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Restaurant> restaurantPage = restaurantCustomRepository.searchBy(name, pageRequest, "name");
        if (!names.contains(name)) {
            try {
                RestaurantPages tripRestaurant = null;
                try {
                    tripRestaurant = externalApiService.test2(name);
                } catch (Exception e) {
                    e.getMessage();
                    System.out.println("TTTTTTTTTTTTTAAAAAAAAAAAAAA      PPPPPPPPPPPPPIIIIIIIIIZZZZZZDAAAAAAAAAA");
                }
                List<Restaurant> restaurants = null;
                if (tripRestaurant != null) {
                    restaurants = tripRestaurant.getRestaurants().stream()
                            .filter(e -> restaurantRepository.findAllByName(e.getName()).isEmpty())
                            .map(restaurantSerializer)
                            .toList();
                }

                try {
                    if (restaurants != null) {
                        restaurantRepository.saveAll(restaurants);
                    }
                } catch (DataIntegrityViolationException e) {
                    e.getMessage();
                }
                if (tripRestaurant != null) {
                    return tripRestaurant;
                }

            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.error("Ресурс не найден: " + e.getStatusText());
                }
            }
        }


        names.add(name);
        return pageMapper.buildRestaurantPage(limit, page, restaurantPage);

    }

    @Transactional
    public RestaurantPages getRestaurantsByName(String name, Boolean findOnKp, Integer page, Integer limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Restaurant> moviePage = restaurantCustomRepository.searchBy(name, pageRequest, "name");
        if (moviePage.isEmpty() || moviePage.get().count() < 0) {
            System.out.println();
            findOnKp = true;
        }
        System.out.println(moviePage.get().count());
        System.out.println(findOnKp);
        if (Boolean.TRUE.equals(findOnKp)) {
            System.out.println("searching");
            RestaurantPages kpMovies = null;
            try {
                kpMovies = externalApiService.test2(name);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            List<Restaurant> movies = kpMovies.getRestaurants().stream()
                    .filter(e -> restaurantRepository.findById(e.getId()).isEmpty())
                    .map(restaurantSerializer)
                    .toList();

            restaurantRepository.saveAll(movies);
            System.out.println("found");
            return kpMovies;
        }
        System.out.println("Finished");
        return pageMapper.buildRestaurantPage(limit, page, moviePage);
    }

}