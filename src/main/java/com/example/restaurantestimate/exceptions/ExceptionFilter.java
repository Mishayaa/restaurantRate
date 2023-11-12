package com.example.restaurantestimate.exceptions;

import com.example.restaurantestimate.dto.ResponseMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;



import java.io.IOException;
import java.time.Instant;


import static com.example.restaurantestimate.constant.ConstUrls.PUBLIC_URLS;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Component
public class ExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        boolean isPrivateUrl = !PUBLIC_URLS.matches(request);

        if (authHeader == null && isPrivateUrl) {
            ResponseMessage appError = new ResponseMessage(
                    FORBIDDEN.value(),
                    "Авторизируйтесь для выполнения этого запроса.",
                    Instant.now().toString());

            response.setStatus(FORBIDDEN.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(UTF_8.toString());
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.writeValue(response.getWriter(), appError);
        }
        filterChain.doFilter(request, response);
    }
}