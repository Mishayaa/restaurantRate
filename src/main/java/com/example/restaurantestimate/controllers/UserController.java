package com.example.restaurantestimate.controllers;


import com.example.restaurantestimate.dto.AuthTokenDtoResponse;
import com.example.restaurantestimate.dto.ResponseMessage;
import com.example.restaurantestimate.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.example.restaurantestimate.dto.user.UserDtoResponse;
import com.example.restaurantestimate.dto.user.UserRegistrationDtoRequest;
import com.example.restaurantestimate.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
public class UserController {
    private UserService userService;
    private AuthenticationService authenticationService;
    public static final String USER_CONTROLLER_PATH = "/api/users";

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @Operation(summary = "Зарегистрировать нового пользователя", description = """
            С помощью этого метода можно зарегистрировать нового пользователя.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Новый пользователь зарегистрирован",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDtoResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Не удалось создать пользователя.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class)
                            )
                    }
            )
    })
    @PostMapping(value = USER_CONTROLLER_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthTokenDtoResponse> createUser(@RequestBody UserRegistrationDtoRequest authRegistrationRequest) {
        return new ResponseEntity<>(authenticationService.createUser(authRegistrationRequest), CREATED);
    }

    @Operation(summary = "Получить пользователя", description = """
            Эндпоинт для получения информации о пользователе.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Существующий пользователь",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDtoResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Пользователь не найден.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class)
                            )
                    }
            )
    })
    @GetMapping(value = USER_CONTROLLER_PATH + "/{userId}")
    public ResponseEntity<UserDtoResponse> getUser(@PathVariable(value = "userId") Long userId) {
        UserDtoResponse user = userService.getUserById(userId);
        return new ResponseEntity<>(user, OK);
    }
}
