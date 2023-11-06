package com.example.restaurantestimate.exceptions;

public class DeactivatedTokenException extends RuntimeException {
    public DeactivatedTokenException(String message) {
        super(message);
    }
}
