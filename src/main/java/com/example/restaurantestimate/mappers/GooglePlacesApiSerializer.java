package com.example.restaurantestimate.mappers;

import com.example.restaurantestimate.dto.google.Candidate;
import com.example.restaurantestimate.dto.restaurant.RestaurantDto;
import com.example.restaurantestimate.dto.restaurant.RestaurantReview;
import com.example.restaurantestimate.entities.Review;
import com.example.restaurantestimate.pojo_tripadvisor.Photo;
import com.example.restaurantestimate.services.GoogleExternalApiService;
import com.example.restaurantestimate.services.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class GooglePlacesApiSerializer implements Function<Candidate, RestaurantDto> {


    @Override
    public RestaurantDto apply(Candidate restaurant) {
        return RestaurantDto.builder()
                .placeID(restaurant.getPlaceId())
                .name(restaurant.getName())
                .formatted_address(restaurant.getFormatted_address())
                .openNow(restaurant.getOpenNow())
                .opening_hours(restaurant.getOpening_hours())
                .geometry(restaurant.getGeometry())
                .posterUrl(restaurant.getPhotoUrl())
                .servesBeer(restaurant.isServesBeer())
                .servesWine(restaurant.isServesWine())
                .hasCocktails(restaurant.isHasCocktails())
                .hasSnacks(restaurant.isHasSnacks())
                .hasTakeaway(restaurant.isHasTakeaway())
                .hasTerrace(restaurant.isHasTerrace())
//                .cuisines(restaurant.getCuisines().stream()
//                        .map(e -> new CuisineDto(e.getName()))
//                        .collect(Collectors.toSet()))
                .rating(restaurant.getRating())
//                .reviews(
//                        reviewService.getRandomReviewsByRestaurantName(restaurant.getName()).stream()
//                                .map(e -> new RestaurantReview(
//                                        e.getId(),
//                                        e.getUser(),
//                                        e.getRating(),
//                                        e.getReview())
//                                ).toList()
//                )
                .build();
    }

//...


}