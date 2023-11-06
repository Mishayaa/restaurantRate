package com.example.restaurantestimate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseMessage {
    private int statusCode;
    private String message;
    private String timestamp;
}
