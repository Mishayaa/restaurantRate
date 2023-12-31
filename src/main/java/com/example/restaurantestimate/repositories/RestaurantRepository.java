package com.example.restaurantestimate.repositories;

import com.example.restaurantestimate.entities.Restaurant;

import com.example.restaurantestimate.entities.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends CrudRepository<Restaurant, Long>, QuerydslPredicateExecutor<Restaurant> {
    Optional<Restaurant> findById(Long id);

    Optional<List<Restaurant>> findAllByName(String name);
    Optional<List<Restaurant>> findAllByPlaceId(String placeID);

    Optional<Restaurant> findByName(String name);
    Optional<Restaurant> findByPlaceId(String placeId);

    List<Restaurant> findAll();

}