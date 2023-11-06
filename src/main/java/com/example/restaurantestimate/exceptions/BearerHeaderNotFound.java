package com.example.restaurantestimate.exceptions;

public class BearerHeaderNotFound extends RuntimeException {
    public BearerHeaderNotFound(String message) {
        super(message);
    }
}