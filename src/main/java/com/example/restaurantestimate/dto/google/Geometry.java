package com.example.restaurantestimate.dto.google;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Geometry {
    private Location location;
    private Viewport viewport;

    // getters and setters
}


