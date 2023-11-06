package com.example.restaurantestimate.exceptions;

public class InvalidJwtSubject extends RuntimeException {
    public InvalidJwtSubject(String message) {
        super(message);
    }
}