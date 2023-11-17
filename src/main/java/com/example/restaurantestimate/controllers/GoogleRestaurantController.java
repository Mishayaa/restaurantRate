package com.example.restaurantestimate.controllers;

import com.example.restaurantestimate.dto.google.Candidate;
import com.example.restaurantestimate.dto.restaurant.RestaurantPages;
import com.example.restaurantestimate.services.GoogleExternalApiService;
import com.example.restaurantestimate.services.GoogleRestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class GoogleRestaurantController {
    private final GoogleExternalApiService restaurantService;

    @GetMapping("/search")
    public Candidate searchRestaurant(
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit) throws Exception {
        return restaurantService.getRestaurantFromGooglePlaces(restaurantService.getPlaceIdFromGooglePlaces(name));
    }

}