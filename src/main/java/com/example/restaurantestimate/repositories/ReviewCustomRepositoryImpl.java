package com.example.restaurantestimate.repositories;


import com.example.restaurantestimate.dto.restaurant.PopularRestaurantDto;
import com.example.restaurantestimate.entities.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl {
    private final EntityManager entityManager;

    public List<PopularRestaurantDto> getPopularRestaurants(Integer count) {
        return entityManager.createQuery("""
                        SELECT r.restaurant as movie, COUNT(r.restaurant) as count
                        FROM Review r
                        GROUP BY r.restaurant
                        """, Tuple.class)
                .setMaxResults(count)
                .getResultList()
                .stream()
                .map(tuple -> {
                    Restaurant restaurant = (Restaurant) tuple.get("restaurant");
                    return PopularRestaurantDto.builder()
                            .id(restaurant.getId())
                            .name(restaurant.getName())
                            .posterUrl(restaurant.getPosterUrl())
                            .reviewCount((Long) tuple.get("count"))
                            .build();
                })
                .sorted(Comparator.comparing(PopularRestaurantDto::getReviewCount))
                .toList();
    }
}