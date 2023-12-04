package com.example.restaurantestimate.controllers;


import com.example.restaurantestimate.dto.AuthTokenDtoResponse;
import com.example.restaurantestimate.dto.ResponseMessage;
import com.example.restaurantestimate.dto.user.UserDtoAboutRequest;
import com.example.restaurantestimate.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
    @RequiredArgsConstructor
    public class UserController {
        private UserService userService;


        private AuthenticationService authenticationService;
        public static final String USER_CONTROLLER_PATH = "/api/users";

        @Autowired
        public void setAuthenticationService(AuthenticationService authenticationService) {
            this.authenticationService = authenticationService;
        }

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
        @PostMapping(value = USER_CONTROLLER_PATH)
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
        @Operation(summary = "Обновить описание пользователя.", description = """
                Эндпоинт предназначен для обновление поля about в сущности пользователя.
                """)
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Обновленная информация о пользователе.",
                        content = {
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserDtoResponse.class)
                                )
                        }
                ),
                @ApiResponse(
                        responseCode = "422",
                        description = "Не удалось обновить информацию о пользователе.",
                        content = {
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ResponseMessage.class)
                                )
                        }
                )
        })
        @PutMapping(value = USER_CONTROLLER_PATH + "/{userId}")
        public ResponseEntity<UserDtoResponse> updateAbout(@PathVariable(value = "userId")
                                                           @Parameter(description = "ID пользователя", example = "1") Long userId,
                                                           @RequestBody UserDtoAboutRequest aboutRequest) {
            return new ResponseEntity<>(userService.updateAbout(userId, aboutRequest), OK);
        }
        @Operation(summary = "Загрузить аватар пользователя.", description = """
            Эндпоинт предназначен для загрузки изображение через form-data. Поддерживает форматы: jpeg, jpg, png, gif.
            """)
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Обновленная информация о пользователе.",
                        content = {
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserDtoResponse.class)
                                )
                        }
                ),
                @ApiResponse(
                        responseCode = "422",
                        description = "Не удалось загрузить изображение.",
                        content = {
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ResponseMessage.class)
                                )
                        }
                )
        })
        @PostMapping(value = USER_CONTROLLER_PATH + "/avatar", consumes = MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<UserDtoResponse> uploadAvatar(@RequestPart("image") MultipartFile multipartFile)
                throws IOException {
            return new ResponseEntity<>(userService.uploadImage(multipartFile), OK);
        }

    }
