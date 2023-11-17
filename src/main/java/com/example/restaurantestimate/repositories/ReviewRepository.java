package com.example.restaurantestimate.repositories;

import com.example.restaurantestimate.entities.Restaurant;
import com.example.restaurantestimate.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByRestaurantId(Long id, PageRequest pageRequest);

    Review findDistinctByRestaurantId(Long restaurantId);

    Page<Review> findAllByUserIdOrderByIdDesc(Long id, PageRequest pageRequest);

    Optional<Review> findAllByUserIdAndRestaurantId(Long userId, Long restaurantId);

    @Query("SELECT r FROM Review r WHERE r.restaurant.name = :name ORDER BY name DESC")
    List<Review> findRandomReviewByRestaurantName(@Param("name") String restaurantName);

    Integer countByRestaurantId(Long movieId);

    @Query("SELECT avg(rating) FROM Review r WHERE r.restaurant.id = :id")
    Double getAvgRatingByRestaurantId(@Param("id") Long restaurantId);


    @Query("SELECT r.restaurant, COUNT(r.restaurant) FROM Review r GROUP BY r.restaurant")
    List<Map<Integer, Restaurant>> getPopularRestaurants();
}