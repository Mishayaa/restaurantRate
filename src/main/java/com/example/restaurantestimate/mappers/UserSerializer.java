package com.example.restaurantestimate.mappers;

import com.example.restaurantestimate.dto.user.UserDtoResponse;
import com.example.restaurantestimate.entities.User;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserSerializer implements Function<User, UserDtoResponse> {
    @Override
    public UserDtoResponse apply(User user) {
        return UserDtoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .about(user.getAbout())
                .build();
    }
}