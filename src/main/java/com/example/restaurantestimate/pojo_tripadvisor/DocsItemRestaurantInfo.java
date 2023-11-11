package com.example.restaurantestimate.pojo_tripadvisor;
import com.example.restaurantestimate.entities.Rating;

import java.util.List;
import java.util.Optional;

public class DocsItemRestaurantInfo {
    private long id;
    private Optional<Rating> rating;
    private String name;
    private Poster poster;
    private List<CuisinesItem> cuisines;

    private Optional<String> description;

    public long getId() {
        return id;
    }
    public List<CuisinesItem> getCuisines() {
        return cuisines;
    }


    public Optional<Rating> getRating() {
        return rating;
    }

    public String getName() {
        return name;
    }
    public Optional<String> getDescription() {
        return description;
    }

    public Poster getPoster() {
        return poster;
    }
}
