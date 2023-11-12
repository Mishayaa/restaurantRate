package com.example.restaurantestimate.jwt;


import com.example.restaurantestimate.dto.ResponseMessage;
import com.example.restaurantestimate.repositories.DeactivatedTokenRepository;
import com.example.restaurantestimate.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.time.Instant;


import static com.example.restaurantestimate.constant.ConstUrls.PUBLIC_URLS;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtTokenUtils jwtTokenUtils;
    private UserService userService;
    private DeactivatedTokenRepository deactivatedTokenRepository;

    @Autowired

    public void setJwtTokenUtils(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Autowired

    public void setDeactivatedTokenRepository(DeactivatedTokenRepository deactivatedTokenRepository) {
        this.deactivatedTokenRepository = deactivatedTokenRepository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (PUBLIC_URLS.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtTokenUtils.extractUsername(jwt);
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(username);
            if (isValidToken(jwt, userDetails) && isActiveToken(jwt)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                ResponseMessage appError = new ResponseMessage(
                        FORBIDDEN.value(),
                        "Токен не валиден",
                        Instant.now().toString());

                response.setStatus(FORBIDDEN.value());
                response.setContentType(APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(UTF_8.toString());
                ObjectMapper objectMapper = new ObjectMapper();

                objectMapper.writeValue(response.getWriter(), appError);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isValidToken(String token, UserDetails userDetails) {
        return jwtTokenUtils.isTokenValid(token, userDetails);
    }

    private boolean isActiveToken(String token) {
        return !deactivatedTokenRepository.existsByToken(token);
    }
}