package com.example.restaurantestimate.exceptions;

    public class CustomJwtExpiredException extends RuntimeException {
        public CustomJwtExpiredException(String message) {
            super(message);
        }
    }
