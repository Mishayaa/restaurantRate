package com.example.restaurantestimate.dto.google;

import com.example.restaurantestimate.dto.google.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Viewport {
    private Location northeast;
    private Location southwest;

    // getters and setters
}