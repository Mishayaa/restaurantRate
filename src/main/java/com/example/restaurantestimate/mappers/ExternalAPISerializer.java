package com.example.restaurantestimate.mappers;


import com.example.restaurantestimate.dto.restaurant.RestaurantDto;
import com.example.restaurantestimate.pojo_tripadvisor.DocsItemRestaurantInfo;
import com.example.restaurantestimate.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ExternalAPISerializer implements Function<DocsItemRestaurantInfo, RestaurantDto> {
    private final ReviewService reviewService;

    @Override
    public RestaurantDto apply(DocsItemRestaurantInfo restaurantInfo) {
        return RestaurantDto.builder()
               // .id(restaurantInfo.getId())
               // .description(restaurantInfo.getDescription().orElse(""))
                .name(restaurantInfo.getName())
//                .averageRating(reviewService.getAverageReviewRatingById(restaurantInfo.getId()))
//                .cuisines(
//                        restaurantInfo.getCuisines().stream()
//                                .map(e -> cuisineService.findCuisine(e.getName()))
//                                .map(e -> new CuisineDto(e.getName()))
//                                .collect(Collectors.toSet())
//                )
                .posterUrl(restaurantInfo.getPoster() == null ? "" : restaurantInfo.getPoster().getUrl())
              //  .reviews(reviewService.getRandomReviewsByRestaurantId(restaurantInfo.getId()).stream()
//                        .map(e -> new RestaurantReview(
//                                e.getId(),
//                                e.getUser(),
//                                e.getRating(),
//                                e.getReview())
//                        ).toList()
//                )
                .build();
    }
}