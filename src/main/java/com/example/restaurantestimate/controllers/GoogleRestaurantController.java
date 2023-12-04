package com.example.restaurantestimate.controllers;

import com.example.restaurantestimate.dto.google.Candidate;
import com.example.restaurantestimate.dto.restaurant.RestaurantPages;
import com.example.restaurantestimate.services.GoogleExternalApiService;
import com.example.restaurantestimate.services.GoogleRestaurantService;
import com.example.restaurantestimate.services.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class GoogleRestaurantController {
    private final GoogleExternalApiService restaurantService;
    private final RestaurantService restaurantService2;

    @GetMapping("/search")
    public RestaurantPages searchRestaurant(
            @RequestParam String name
    ) throws Exception {
        return restaurantService.test2(name);
    }

}