package com.example.restaurantestimate.dto.restaurant;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDtoShort {
    private String name;
    private Long id;
    private String placeId;
    private String posterUrl;

}