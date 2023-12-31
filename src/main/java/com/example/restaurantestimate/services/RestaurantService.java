package com.example.restaurantestimate.services;

import com.example.restaurantestimate.dto.ResponseMessage;
import com.example.restaurantestimate.dto.google.Candidate;
import com.example.restaurantestimate.dto.restaurant.*;
import com.example.restaurantestimate.dto.user.UserRestaurant;
import com.example.restaurantestimate.entities.Restaurant;
import com.example.restaurantestimate.entities.User;
import com.example.restaurantestimate.mappers.GooglePlacesApiSerializer;
import com.example.restaurantestimate.mappers.PageMapper;
import com.example.restaurantestimate.mappers.RestaurantMapper;
import com.example.restaurantestimate.mappers.RestaurantSerializer;

import com.example.restaurantestimate.repositories.RestaurantCustomRepositoryImpl;
import com.example.restaurantestimate.repositories.RestaurantRepository;
import com.example.restaurantestimate.repositories.ReviewCustomRepositoryImpl;
import com.example.restaurantestimate.repositories.filters.UserIdFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;


import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantCustomRepositoryImpl restaurantCustomRepository;
    private final RestaurantSerializer restaurantSerializer;
    private final GooglePlacesApiSerializer googlePlacesApiSerializer;

    private final GoogleExternalApiService googleExternalApiService;
    private final RestaurantMapper restaurantMapper;
    private final PageMapper pageMapper;
    private final UserService userService;
    private final ReviewService reviewService;

    private final ReviewCustomRepositoryImpl reviewCustomRepository;

    @Transactional
    public RestaurantCard addToFavorite(Long restaurantId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // Пользователь аутентифицирован
            String username = authentication.getName();
            User user = reviewService.getCurrentUser();
            System.out.println(username);
            System.out.println(user.getUsername());
            Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                    new EntityNotFoundException(String.format("Restaurant with such ID: '%s' not found!", restaurantId)));
            System.out.println(user.getUsername());
            user.getFavorites().add(restaurant);
            userService.updateUser(user);
            return restaurantMapper.toRestaurantCard(restaurant, user.getId());

        } else {
            // Пользователь не аутентифицирован
            throw new RuntimeException("Пользователь не аутентифицирован");
        }


    }

    @Transactional
    public FavoritesRestaurantsPage getFavorites(Long userId, Integer page, Integer limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Restaurant> restaurantsPages = userService.getUserFavorites(userId, pageRequest);

        List<RestaurantCard> restaurants = restaurantsPages.getContent().stream()
                .map(e -> restaurantMapper.toRestaurantCard(e, userId))
                .toList();

        FavoritesRestaurantsPage restaurantPage = new FavoritesRestaurantsPage(restaurants);
        restaurantPage.setPage(page);
        restaurantPage.setPages(restaurantsPages.getTotalPages());
        restaurantPage.setTotal(restaurantsPages.getTotalElements());
        restaurantPage.setLimit(limit);

        return restaurantPage;
    }

    public ResponseMessage deleteFromFavorites(Long restaurantId) {
        User user = userService.getCurrentUser();
        Set<Restaurant> updatedFavorites = user.getFavorites().stream()
                .filter(e -> !e.getId().equals(restaurantId))
                .collect(Collectors.toSet());

        user.setFavorites(updatedFavorites);
        userService.updateUser(user);

        return ResponseMessage.builder()
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Restaurant with  ID: '%s' was successfully deleted from favourites.", restaurantId))
                .timestamp(Instant.now().toString())
                .build();
    }

    @Transactional
    public RestaurantCard getRestaurantById(Long id) {
        System.out.println("I am used");
        Optional<Restaurant> restaurantInDb = restaurantRepository.findById(id);
        Restaurant restaurant = restaurantInDb.orElseThrow(() ->
                new EntityNotFoundException(String.format("Restaurant with such ID: '%s' was not found!", id)));
        User user = userService.getCurrentUser();
        if (user != null) {
            return restaurantMapper.toRestaurantCard(restaurant, user.getId());
        } else {
            return restaurantMapper.toRestaurantCard(restaurant);

        }

    }

    @Transactional
    public RestaurantCard getRestaurantByPlaceId(String id) throws Exception {
        User user = userService.getCurrentUser();
        Optional<Restaurant> restaurantInDb = restaurantRepository.findByPlaceId(id);
//        if (restaurantInDb.isEmpty()) {
//            Restaurant restaurant = restaurantSerializer.apply(googlePlacesApiSerializer.apply(googleExternalApiService.getRestaurantFromGooglePlaces(id)));
//            restaurantInDb = Optional.of(restaurant);
//        }

        if (user != null) {
            return restaurantMapper.toRestaurantCard(restaurantInDb.get(), user.getId());
        } else {
            return restaurantMapper.toRestaurantCard(restaurantInDb.get());

        }

    }

    @Transactional
    public RestaurantPages getRestaurantsByName(String name, Boolean findOnTrip, Integer page, Integer limit) {
        try {
            if (Boolean.TRUE.equals(findOnTrip)) {
                if (Boolean.TRUE.equals(findOnTrip)) {
                    RestaurantPages tripRestaurant = googleExternalApiService.findRestaurantByName(name, page, limit);
                    List<Restaurant> restaurants = tripRestaurant.getRestaurants().stream()
                            .filter(e -> restaurantRepository.findById(e.getId()).isEmpty())
                            .map(restaurantSerializer)
                            .toList();

                    restaurantRepository.saveAll(restaurants);

                    return tripRestaurant;
                }

            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.error("Ресурс не найден: " + e.getStatusText());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Restaurant> restaurantPage = restaurantCustomRepository.searchBy(name, pageRequest, "name");
        return pageMapper.buildRestaurantPage(limit, page, restaurantPage);
    }

    @Transactional
    public RestaurantPagesShort getRestaurantByNameShortInfo(String name, Integer page, Integer limit) {

        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Restaurant> restaurantPage = restaurantCustomRepository.searchBy(name, pageRequest, "name");
        return pageMapper.buildRestaurantPageShort(limit, page, restaurantPage);
    }

    @Transactional
    public RestaurantPagesShort getRestaurantsByNameShortInfo(String name, Boolean findOnKp, Integer page, Integer limit) {

        if (Boolean.TRUE.equals(findOnKp)) {
            RestaurantPages kpMovies = null;
            try {
                kpMovies = googleExternalApiService.test2(name);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            List<Restaurant> movies = kpMovies.getRestaurants().stream()
                    .filter(e -> restaurantRepository.findByPlaceId(e.getPlaceID()).isEmpty())
                    .map(restaurantSerializer)
                    .toList();
            restaurantRepository.saveAll(movies);
            return pageMapper.buildMoviePageShortFromKp(limit, page, kpMovies, movies);
        }
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Restaurant> moviePage = restaurantCustomRepository.searchBy(name, pageRequest, "name");
        return pageMapper.buildRestaurantPageShort(limit, page, moviePage);
    }

    @Transactional
    public UserRestaurantPage getRestaurantsByUser(Long userId, Integer page, Integer limit) {
        User user = userService.findById(userId);
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Restaurant> restaurants = restaurantCustomRepository.findByUserIdFilter(new UserIdFilter(user.getId()), pageRequest);

        List<UserRestaurant> userRestaurants = restaurants.getContent().stream()
                .map(e -> restaurantMapper.toUserRestaurantDto(e, userId))
                .toList();

        UserRestaurantPage userRestaurantsPage = UserRestaurantPage.builder()
                .user(userService.getUserById(userId))
                .restaurants(userRestaurants)
                .build();
        userRestaurantsPage.setTotal(restaurants.getTotalElements());
        userRestaurantsPage.setPages(restaurants.getTotalPages());
        userRestaurantsPage.setPage(page);
        userRestaurantsPage.setLimit(limit);

        return userRestaurantsPage;
    }

    public List<RestaurantNameId> getRestaurantsName() {
        Map<Long, String> restaurants = restaurantCustomRepository.getNamesWithId();
        List<RestaurantNameId> response = new ArrayList<>();
        restaurants.forEach((k, v) -> {
            RestaurantNameId restaurant = new RestaurantNameId(k, v);
            response.add(restaurant);
        });
        return response;
    }


    public List<PopularRestaurantDto> getPopularRestaurants(Integer count) {
        return reviewCustomRepository.getPopularRestaurants(count);
    }

}
