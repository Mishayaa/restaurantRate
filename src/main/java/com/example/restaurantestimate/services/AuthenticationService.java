package com.example.restaurantestimate.services;

import com.example.restaurantestimate.configs.EncoderConfig;
import com.example.restaurantestimate.dto.AuthTokenDtoResponse;
import com.example.restaurantestimate.dto.ResponseMessage;
import com.example.restaurantestimate.dto.user.UserDtoLoginRequest;
import com.example.restaurantestimate.dto.user.UserRegistrationDtoRequest;
import com.example.restaurantestimate.entities.DeactivatedToken;
import com.example.restaurantestimate.entities.Token;
import com.example.restaurantestimate.entities.User;
import com.example.restaurantestimate.exceptions.BearerHeaderNotFound;
import com.example.restaurantestimate.exceptions.DeactivatedTokenException;
import com.example.restaurantestimate.exceptions.EntityAlreadyExistException;
import com.example.restaurantestimate.exceptions.InvalidJwtSubject;
import com.example.restaurantestimate.jwt.JwtToken;
import com.example.restaurantestimate.mappers.AccessTokenSerializer;
import com.example.restaurantestimate.repositories.DeactivatedTokenRepository;
import com.example.restaurantestimate.repositories.TokenRepository;
import com.example.restaurantestimate.jwt.JwtTokenUtils;
import com.example.restaurantestimate.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final AccessTokenSerializer accessTokenSerializer;
    private final UserRepository userRepository;

    private UserService userService;
    private final TokenRepository tokenRepository;
    private final DeactivatedTokenRepository deactivatedTokenRepository;
    private final RoleService roleService;

    private EncoderConfig encoderConfig;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Transactional
    public AuthTokenDtoResponse authenticate(UserDtoLoginRequest authLoginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authLoginRequest.getUsername(),
                        authLoginRequest.getPassword()
                )
        );
        UserDetails userDetails = userService.loadUserByUsername(authLoginRequest.getUsername());
        User user = userService.findByUsername(userDetails.getUsername());

        JwtToken accessToken = jwtTokenUtils.createToken(user, userDetails);
        String accessTokenString = accessTokenSerializer.apply(accessToken);

        Token tokens = Token.builder()
                .accessToken(accessTokenString)
                .accessTokenExpiry(accessToken.getExpiredAt().toString())
                .build();

        tokenRepository.save(tokens);

        return AuthTokenDtoResponse.builder()
                .accessToken(accessTokenString)
                .build();
    }

    @Transactional
    public AuthTokenDtoResponse createUser(UserRegistrationDtoRequest authRegistrationRequest) {
        Optional<User> userInDbWithUsername = userRepository.findByUsername(authRegistrationRequest.getUsername());
        Optional<User> userInDbWithEmail = userRepository.findByEmail(authRegistrationRequest.getEmail());
        if (userInDbWithUsername.isPresent() || userInDbWithEmail.isPresent()) {
            throw new EntityAlreadyExistException("Пользователь с таким именем или email уже существует");
        }

        User user = new User();
        user.setEmail(authRegistrationRequest.getEmail());
        user.setUsername(authRegistrationRequest.getUsername());
        user.setName(authRegistrationRequest.getName());
        user.setPassword(encoderConfig.passwordEncoder().encode(authRegistrationRequest.getPassword()));
        user.setRoles(List.of(roleService.getUserRole()));
        userRepository.save(user);
        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        JwtToken accessToken = jwtTokenUtils.createToken(user, userDetails);
        String accessTokenString = accessTokenSerializer.apply(accessToken);

        Token tokens = Token.builder()
                .accessToken(accessTokenString)
                .accessTokenExpiry(accessToken.getExpiredAt().toString())
                .build();

        tokenRepository.save(tokens);

        return AuthTokenDtoResponse.builder()
                .accessToken(accessTokenString)
                .build();
    }

    public AuthTokenDtoResponse refreshToken(HttpServletRequest request) throws IOException {
        final String header = request.getHeader(AUTHORIZATION);
        final String jwtToken = header.substring(7);

        String username = jwtTokenUtils.extractUsername(jwtToken);

        if (username != null) {
            UserDetails userDetails = userService.loadUserByUsername(username);

            if (jwtTokenUtils.isTokenValid(jwtToken, userDetails)
                    && Boolean.FALSE.equals(deactivatedTokenRepository.existsByToken(jwtToken))) {
                deactivatedTokenRepository.save(new DeactivatedToken(
                        jwtToken,
                        Date.from(Instant.now()))
                );

                JwtToken accessToken = jwtTokenUtils.refreshToken(jwtToken);
                String accessTokenString = accessTokenSerializer.apply(accessToken);

                Token tokens = Token.builder()
                        .accessToken(accessTokenString)
                        .accessTokenExpiry(accessToken.getExpiredAt().toString())
                        .build();

                tokenRepository.save(tokens);

                return AuthTokenDtoResponse.builder()
                        .accessToken(accessTokenString)
                        .build();
            } else {
                throw new DeactivatedTokenException("Данный токен деактивирован, "
                        + "залогиньтесь заного чтобы получить корректный токен.");
            }
        }
        throw new InvalidJwtSubject("Имя пользователя в токене null.");
    }


    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        final String header = request.getHeader(AUTHORIZATION);
        final String jwtToken;
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BearerHeaderNotFound("В запросе не указан заголовок с токеном!");
        }

        jwtToken = header.substring(7);
        Optional<Token> token = tokenRepository.findByAccessToken(jwtToken);

        if (token.isPresent()) {
            deactivatedTokenRepository.save(new DeactivatedToken(
                    token.get().getAccessToken(), Date.from(Instant.now())));

            authentication.setAuthenticated(false);
            SecurityContextHolder.clearContext();

            ResponseMessage appError = new ResponseMessage(
                    FORBIDDEN.value(),
                    "Вы успешно вышли из профиля",
                    Instant.now().toString());

            response.setStatus(FORBIDDEN.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(UTF_8.toString());
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.writeValue(response.getWriter(), appError);

        } else {
            throw new EntityNotFoundException("Данный токен не зарегистрирован в приложении.");
        }
    }
}