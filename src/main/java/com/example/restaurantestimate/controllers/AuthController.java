package com.example.restaurantestimate.controllers;

import com.example.restaurantestimate.dto.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.example.restaurantestimate.dto.AuthTokenDtoResponse;
import com.example.restaurantestimate.dto.user.UserDtoLoginRequest;
import com.example.restaurantestimate.services.AuthenticationService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    public static final String AUTH_CONTROLLER_LOGIN_PATH = "/api/auth/login";
    public static final String AUTH_CONTROLLER_REFRESH_TOKEN_PATH = "/api/auth/refresh";
    public static final String AUTH_CONTROLLER_LOGOUT_PATH = "/api/auth/logout";



    @Operation(summary = "Аутентификация пользователя", description = """
            С помощью этого метода происходит аутентификация пользователя в приложении.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Аутентификация успешна",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthTokenDtoResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Не удалось авторизироваться с такими данными",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class)
                            )
                    }
            )
    })
        @PostMapping(AUTH_CONTROLLER_LOGIN_PATH)
        public ResponseEntity<AuthTokenDtoResponse> login(@RequestBody UserDtoLoginRequest authLoginRequest) {
            return ResponseEntity.ok(authenticationService.authenticate(authLoginRequest));
        }



    @Operation(summary = "Получение нового токена доступа", description = """
            С помощью этого метода можно обновить токен доступа для защищенных эндпоинтов.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Токены для этого пользователя успешно обновлненны",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthTokenDtoResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Рефреш токен истек, нужно залогиниться заново",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessage.class)
                            )
                    }
            )
    })
    @GetMapping(AUTH_CONTROLLER_REFRESH_TOKEN_PATH)
    public ResponseEntity<AuthTokenDtoResponse> refreshToken(HttpServletRequest request) throws IOException {
        AuthTokenDtoResponse response = authenticationService.refreshToken(request);
        return new ResponseEntity<>(response, OK);

    }
    @GetMapping("/api/auth/login/home")
    public String  home()  {
        return "Hello epta";

    }


    @Operation(summary = "Логаут пользователя", description = """
            С помощью этого метода производиться выход пользователя из приложения.
            \s
            Токен доступа заноситься в деактивированные токены, поэтому требуется пройти процедуру логина.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно вышел")
    })
    @GetMapping(AUTH_CONTROLLER_LOGOUT_PATH)
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        authenticationService.logout(request, response, authentication);
    }
}