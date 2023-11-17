package com.example.restaurantestimate.controllers;

import com.example.restaurantestimate.dto.ResponseMessage;
import com.example.restaurantestimate.dto.ReviewDtoRequest;
import com.example.restaurantestimate.dto.ReviewDtoUpdateRequest;
import com.example.restaurantestimate.dto.review.ReviewDto;
import com.example.restaurantestimate.dto.review.ReviewPages;
import com.example.restaurantestimate.services.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    public static final String REVIEW_CONTROLLER_PATH = "/api/reviews";

    @Operation(summary = "Create review for restaurant", description =
            """
          Answer can have empty review и rating
            \s
            In such way user will be able to add restaurant to his profile and make review after visit .
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Review was successfully added",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReviewPages.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Review is already created or  ID not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class))
                    }
            )
    })
    @PostMapping(value = REVIEW_CONTROLLER_PATH)
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDtoRequest reviewDtoRequest) {
        return new ResponseEntity<>(reviewService.createReview(reviewDtoRequest), CREATED);
    }
    @Operation(summary = "Search for review in Db", description =
            """
            Search of review is possible with such params:
            \s
            1) userId
            \s
            2) restaurantId
            \s
            3) userId + restaurantId
            \s
           
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Users review on restaurants",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewPages.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Review on restaurant with such ID from user with such ID not found.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class))
                    }
            ),
    })

    @GetMapping(value = REVIEW_CONTROLLER_PATH)
    public ResponseEntity<Object> getReviews(
            @RequestParam(required = false) @Parameter(description = "users ID",
                    example = "1") Long userId,
            @RequestParam(required = false) @Parameter(description = "restaurant ID",
                    example = "1") Long restaurantId,
            @RequestParam(required = false, value = "page", defaultValue = "1")
            @Parameter(description = "Selection page.") Integer page,
            @RequestParam(required = false, value = "limit", defaultValue = "10")
            @Parameter(description = "Quantity of elements on page.") Integer limit) {

        if (userId != null && restaurantId != null) {
            return new ResponseEntity<>(reviewService.getReviewByUserIdAndMovieId(userId, restaurantId), OK);
        } else if (restaurantId != null) {
            return new ResponseEntity<>(reviewService.getReviewByRestaurantId(restaurantId, page, limit), OK);
        } else if (userId != null) {
            return new ResponseEntity<>(reviewService.getReviewByUserId(userId, page, limit), OK);
        }
        return new ResponseEntity<>(reviewService.getAllReviews(page, limit), OK);
    }


    @Operation(summary = "Update review for restaurant")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "review was successfully added",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReviewPages.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "review or restaurant with such ID not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class))
                    }
            )
    })
    @PutMapping(value = REVIEW_CONTROLLER_PATH + "/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable @Parameter(description = "review ID", example = "1") Long reviewId,
            @RequestBody ReviewDtoUpdateRequest reviewDtoRequest) {
        return new ResponseEntity<>(reviewService.updateReview(reviewId, reviewDtoRequest), OK);
    }
    @Operation(summary = "delete review")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "review successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "exception during request .",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class))
                    }
            )
    })
    @DeleteMapping(value = REVIEW_CONTROLLER_PATH + "/{reviewId}")
    public ResponseEntity<ResponseMessage> deleteReview(@PathVariable(value = "reviewId")
                                                        @Parameter(description = "ID ревью", example = "1")
                                                        Long reviewId) {
        return new ResponseEntity<>(reviewService.deleteReview(reviewId), OK);
    }
}
