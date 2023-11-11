package com.example.restaurantestimate.repositories;

import com.example.restaurantestimate.entities.QReview;
import com.example.restaurantestimate.entities.QUser;
import com.example.restaurantestimate.entities.Restaurant;

import com.example.restaurantestimate.repositories.filters.CuisineFilter;
import com.example.restaurantestimate.repositories.filters.UserIdFilter;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;

import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.restaurantestimate.entities.QRestaurant.restaurant;

@Component
@RequiredArgsConstructor
public class RestaurantCustomRepositoryImpl {
    private final EntityManager entityManager;

    public PageImpl<Restaurant> searchBy(String text, PageRequest pageRequest, String... fields) {
        SearchResult<Restaurant> result = getSearchResult(text, pageRequest, fields);
        long total = result.total().hitCount();
        List<Restaurant> res = result.hits();
        return new PageImpl<>(res, pageRequest, total);
    }

    private SearchResult<Restaurant> getSearchResult(String text, PageRequest pageRequest, String... fields) {
        SearchSession searchSession = Search.session(entityManager);

        return searchSession
                .search(Restaurant.class)
                .where(f -> f.match().fields(fields).matching(text))
                .fetch((int) pageRequest.getOffset(), pageRequest.getPageSize());
    }

    public Map<Long, String> getNamesWithId() {
        return entityManager.createQuery("""
                        SELECT m.id as id, m.name as name
                        FROM Restaurant m
                        ORDER BY m.name LIMIT 20
                        """, Tuple.class)
                .getResultList()
                .stream()
                .collect(
                        Collectors.toMap(
                                tuple -> ((Long) tuple.get("id")),
                                tuple -> ((String) tuple.get("title"))
                        )
                );
    }

    public PageImpl<Restaurant> findByCuisineNameFilter(CuisineFilter filter, Pageable pageable) {
        JPAQuery<Restaurant> query = new JPAQuery<>(entityManager)
                .select(restaurant)
                .from(restaurant)
                .where(restaurant.cuisines.any().name.toLowerCase().contains(filter.getCuisineName().toLowerCase()));

        long total = query.fetchCount();
        query.limit(pageable.getPageSize());
        query.offset(pageable.getOffset());
        List<Restaurant> restaurants = query.fetch();

        return new PageImpl<>(restaurants, pageable, total);
    }

    public PageImpl<Restaurant> findByUserIdFilter(UserIdFilter filter, Pageable pageable) {
        QUser user = QUser.user;
        QReview review = QReview.review;

        JPAQuery<Restaurant> query = new JPAQuery<>(entityManager)
                .select(restaurant)
                .from(restaurant)
                .join(review).on(restaurant.id.eq(review.restaurant.id))
                .join(user).on(user.id.eq(review.user.id))
                .where(review.user.id.eq(filter.getUserId()))
                .orderBy(review.id.desc());

        long total = query.fetchCount();
        query.limit(pageable.getPageSize());
        query.offset(pageable.getOffset());
        List<Restaurant> restaurants = query.fetch();

        return new PageImpl<>(restaurants, pageable, total);
    }
}