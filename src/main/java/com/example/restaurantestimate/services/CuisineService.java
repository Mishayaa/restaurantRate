package com.example.restaurantestimate.services;

import com.example.restaurantestimate.entities.Cuisine;
import com.example.restaurantestimate.repositories.CuisineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuisineService {
    private final CuisineRepository cuisineRepository;

    public Cuisine findCuisine(String cuisineName) {
        Optional<Cuisine> cuisineInDB = cuisineRepository.findByName(cuisineName);
        if (cuisineInDB.isPresent()) {
            return cuisineInDB.get();
        }
        Cuisine newCuisine = Cuisine.builder().name(cuisineName).build();
        return cuisineRepository.save(newCuisine);
    }
}