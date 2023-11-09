package com.example.restaurantestimate.jwt;

import com.example.restaurantestimate.entities.User;
import com.example.restaurantestimate.exceptions.CustomJwtExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.io.Decoders;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.function.Function;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenUtils {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("24")
    private long lifetime;

    public JwtToken createToken(User user, UserDetails userDetails) {
        return JwtToken.builder()
                .id(UUID.randomUUID())
                .subject(userDetails.getUsername())
                .userId(user.getId())
                .authorities(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .createdAt(Instant.now())
                .expiredAt(Instant.now().plus(lifetime,DAYS))
                .build();
    }

    public JwtToken refreshToken(String jwtToken) {
        Claims claims = extractAllClaims(jwtToken);
        String subject = extractUsername(jwtToken);
        List<String> roles = extractRoles(jwtToken);
        Long userId = claims.get("userId", Long.class);

        return JwtToken.builder()
                .id(UUID.randomUUID())
                .subject(subject)
                .userId(userId)
                .authorities(roles)
                .createdAt(Instant.now())
                .expiredAt(Instant.now().plus(lifetime, DAYS))
                .build();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return (ArrayList<String>) claims.get("role");
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            throw new CustomJwtExpiredException("Токен доступа истек, обновите токен доступа.");
        } catch (MalformedJwtException e) {
            log.error(e.getMessage());
            throw new MalformedJwtException("Токен доступа некорректный.");
        }

    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}