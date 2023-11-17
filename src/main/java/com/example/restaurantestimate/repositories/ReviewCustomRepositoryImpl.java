package com.example.restaurantestimate.repositories;


import com.example.restaurantestimate.dto.restaurant.PopularRestaurantDto;
import com.example.restaurantestimate.entities.Restaurant;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.stereotype.Component;


import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl {
    private final EntityManager entityManager;

    public List<PopularRestaurantDto> getPopularRestaurants(Integer count) {

//        List<Restaurant> restaurants = entityManager.createQuery("SELECT r FROM Restaurant r", Restaurant.class)
//                .setMaxResults(4)
//                .getResultList();
        List<Restaurant> restaurants = entityManager.createQuery("SELECT r FROM Restaurant r ORDER BY r.reviews.size DESC", Restaurant.class)
                .setMaxResults(count)
                .getResultList();

        List<PopularRestaurantDto> result = new ArrayList<>();
        for (Restaurant rest : restaurants) {
            result.add(PopularRestaurantDto.builder()
                    .id(rest.getId())
                    .name(rest.getName())
                    .posterUrl(rest.getPosterUrl()).build());
        }
        return result;

    }
}