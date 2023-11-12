package com.example.restaurantestimate.controllers;

import com.example.restaurantestimate.dto.PageDto;
import com.example.restaurantestimate.dto.ResponseMessage;
import com.example.restaurantestimate.dto.restaurant.*;
import com.example.restaurantestimate.services.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    public static final String RESTAURANT_CONTROLLER_PATH = "/api/restaurant";
    public static final String RESTAURANT_CONTROLLER_PATH_USERS = "/api/restaurant/user";
    public static final String RESTAURANT_CONTROLLER_PATH_CUISINE = "/api/restaurant/cuisine";

    @Operation(summary = "Get restaurant by ID from app db")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurant with such ID",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RestaurantCard.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Restaurant with such ID was  not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class)
                            )
                    }
            )
    })
    @GetMapping(value = RESTAURANT_CONTROLLER_PATH + "/{restaurantId}")
    public ResponseEntity<RestaurantCard> getRestaurantById(@PathVariable("restaurantId")
                                                            @Parameter(description = "restaurant ID", example = "1") Long restaurantId,
                                                            @RequestParam(required = false, value = "findRst", defaultValue = "false")
                                                            @Parameter(description = "Search by tripAdvisor. true - search on tripadvisor, "
                                                                    + "false - in app bd.") Boolean findRst) {
        RestaurantCard restaurant = restaurantService.getRestaurantById(restaurantId, findRst);
        return new ResponseEntity<>(restaurant, OK);
    }


    @Operation(summary = "Get all users restaurants", description =
            """
                    Return all users restaurants which he added to his collection with his review and rating

                    """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All restaurants from db which pinned with user",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserRestaurantPage.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "User not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class)
                            )
                    }
            )
    })
    @GetMapping(value = RESTAURANT_CONTROLLER_PATH_USERS + "/{userId}")
    public ResponseEntity<UserRestaurantPage> getRestaurantsByUser(
            @PathVariable(value = "userId") @Parameter(description = "users ID",
                    example = "1") Long userId,
            @RequestParam(required = false, value = "page", defaultValue = "1")
            @Parameter(description = "Selecting page.") Integer page,
            @RequestParam(required = false, value = "limit", defaultValue = "10")
            @Parameter(description = "Quantity of elements on page.") Integer limit) {

        UserRestaurantPage restaurants = restaurantService.getRestaurantsByUser(userId, page, limit);
        return new ResponseEntity<>(restaurants, OK);
    }

    @Operation(summary = "Add movie to favourites")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurant data were added",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RestaurantCard.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "User with such ID not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class)
                            )
                    }
            )
    })
    @PostMapping(value = RESTAURANT_CONTROLLER_PATH + "/favorites")
    public ResponseEntity<RestaurantCard> addToFavorite(@RequestParam
                                                        @Parameter(description = "ID restorana", example = "1") Long restaurantId) {
        RestaurantCard restaurantCard = restaurantService.addToFavorite(restaurantId);
        return new ResponseEntity<>(restaurantCard, OK);
    }

    @Operation(summary = "Get list of favourite movies")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of favourites",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = RestaurantCard.class))
                            )
                    }
            )
    })
    @GetMapping(value = RESTAURANT_CONTROLLER_PATH + "/favorites")
    public ResponseEntity<FavoritesRestaurantsPage> getFavorites(
            @RequestParam
            @Parameter(description = "users ID", example = "1") Long userId,
            @RequestParam(required = false, value = "page", defaultValue = "1")
            @Parameter(description = "Selection page.") Integer page,
            @RequestParam(required = false, value = "limit", defaultValue = "10")
            @Parameter(description = "Elements quantity on this page.") Integer limit) {
        FavoritesRestaurantsPage restaurants = restaurantService.getFavorites(userId, page, limit);
        return new ResponseEntity<>(restaurants, OK);
    }

    @Operation(summary = "Delete from favourites")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurant with such id was deleted from chosen",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Restaurant with such ID not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class)
                            )
                    }
            )
    })
    @DeleteMapping(value = RESTAURANT_CONTROLLER_PATH + "/favorites")
    public ResponseEntity<ResponseMessage> deleteFromFavorite(@RequestParam
                                                              @Parameter(description = "restaurantID", example = "1") Long restaurantId) {
        return new ResponseEntity<>(restaurantService.deleteFromFavorites(restaurantId), OK);
    }

    @Operation(summary = "Universal search", description =
            """
                    Search restaurant from DB or from TripAdvisorApi.

                     """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurants that match given param",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RestaurantPages.class)
                            )
                    }
            )
    })
    @GetMapping(value = RESTAURANT_CONTROLLER_PATH)
    public ResponseEntity<PageDto> getRestaurantsByName(
            @RequestParam(value = "name") @Parameter(description = "Name",
                    example = "Ukraine") String name,
            @RequestParam(required = false, value = "page", defaultValue = "1")
            @Parameter(description = "SelectionPage.") Integer page,
            @RequestParam(required = false, value = "limit", defaultValue = "10")
            @Parameter(description = "Quantity of elements on page.") Integer limit,
            @RequestParam(required = false, value = "expanded", defaultValue = "false")
            @Parameter(description = "Give full info about restaurants."
                    + "true - return full info about restaurants, "
                    + "false - only from drop-down list.") Boolean expanded,
            @RequestParam(required = false, value = "findOnTrip", defaultValue = "false")
            @Parameter(description = "Search by tripadvisor. true - search on tripadvisor, "
                    + "false - in app db.") Boolean findOnTrip) {

        if (Boolean.TRUE.equals(expanded)) {
            return new ResponseEntity<>(restaurantService.getRestaurantsByName(name, findOnTrip, page, limit), OK);
        }
        return new ResponseEntity<>(restaurantService.getRestaurantByNameShortInfo(name, findOnTrip, page, limit), OK);
    }

    @Operation(summary = "Get restaurant by cuisine.", description =
            """
                    Return all restaurants with such cuisine.
                                        """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All restaurants from db with such cuisine",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RestaurantPages.class)
                            )
                    }
            )
    })
    @GetMapping(value = RESTAURANT_CONTROLLER_PATH_CUISINE)
    public ResponseEntity<RestaurantPages> findRestaurantByCuisine(
            @RequestParam(value = "cuisineName") @Parameter(description = "Cuisine.",
                    example = "Italian.") String cuisineName,
            @RequestParam(required = false, value = "page", defaultValue = "1")
            @Parameter(description = "Selection Page.") Integer page,
            @RequestParam(required = false, value = "limit", defaultValue = "10")
            @Parameter(description = "Quantity of elem on page.") Integer limit) {

        return new ResponseEntity<>(restaurantService.getRestaurantsByCuisine(cuisineName, page, limit), OK);
    }

    @Operation(summary = "Get names")
    @GetMapping(value = RESTAURANT_CONTROLLER_PATH + "/restaurantsNames")
    public ResponseEntity<List<RestaurantNameId>> findRestaurantsNames() {
        return new ResponseEntity<>(restaurantService.getRestaurantsName(), OK);
    }

    @Operation(summary = "Get 5 restaurants with the most reviews.", description =
            """
                    Returns 5 restaurants with the most reviews
                    """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurants from DB with the most reviews.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PopularRestaurantDto.class))
                            )
                    }
            )
    })
    @GetMapping(value = RESTAURANT_CONTROLLER_PATH + "/popular")
    public ResponseEntity<List<PopularRestaurantDto>> getPopularRestaurants(@RequestParam(required = false, defaultValue = "4")
                                                                       Integer count) {
        return new ResponseEntity<>(restaurantService.getPopularRestaurants(count), OK);
    }

}