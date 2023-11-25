package com.example.restaurantestimate.constant;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static com.example.restaurantestimate.controllers.AuthController.AUTH_CONTROLLER_LOGIN_PATH;
import static com.example.restaurantestimate.controllers.AuthController.AUTH_CONTROLLER_REFRESH_TOKEN_PATH;
//import static com.example.restaurantestimate.controllers.RestaurantController.RESTAURANT_CONTROLLER_PATH;
//import static com.example.restaurantestimate.controllers.RestaurantController.RESTAURANT_CONTROLLER_PATH_USERS;
import static com.example.restaurantestimate.controllers.RestaurantController.RESTAURANT_CONTROLLER_PATH;
import static com.example.restaurantestimate.controllers.RestaurantController.RESTAURANT_CONTROLLER_PATH_USERS;
import static com.example.restaurantestimate.controllers.UserController.USER_CONTROLLER_PATH;
import static java.lang.invoke.VarHandle.AccessMode.GET;
import static javax.swing.text.html.FormSubmitEvent.MethodType.POST;

public class ConstUrls {
    public static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher(AUTH_CONTROLLER_REFRESH_TOKEN_PATH, GET.toString()),
            new AntPathRequestMatcher(AUTH_CONTROLLER_REFRESH_TOKEN_PATH, GET.toString()),
            new AntPathRequestMatcher(AUTH_CONTROLLER_LOGIN_PATH, POST.toString()),
            new AntPathRequestMatcher(AUTH_CONTROLLER_LOGIN_PATH, GET.toString()),
            new AntPathRequestMatcher(USER_CONTROLLER_PATH, POST.toString()),
            new AntPathRequestMatcher("/**", GET.toString()),
            new AntPathRequestMatcher("/**", POST.toString()),

            new AntPathRequestMatcher(RESTAURANT_CONTROLLER_PATH+"/restaurantsNames",GET.toString()),
            new AntPathRequestMatcher(RESTAURANT_CONTROLLER_PATH_USERS+"/**",GET.toString()),
            new AntPathRequestMatcher(RESTAURANT_CONTROLLER_PATH+"/popular",GET.toString()),
            new AntPathRequestMatcher(RESTAURANT_CONTROLLER_PATH+"/favorites"),
            new AntPathRequestMatcher(RESTAURANT_CONTROLLER_PATH+"/*"),
            new AntPathRequestMatcher("/api/auth/login/home"),
            new AntPathRequestMatcher("/h2/**"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/swagger-ui.html"),
            new AntPathRequestMatcher("/swagger-resources/**"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/api-docs.html")
    );

}
