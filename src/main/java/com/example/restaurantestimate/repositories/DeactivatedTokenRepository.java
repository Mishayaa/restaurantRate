package com.example.restaurantestimate.repositories;

import com.example.restaurantestimate.entities.DeactivatedToken;
import com.example.restaurantestimate.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface DeactivatedTokenRepository extends JpaRepository<DeactivatedToken, Long> {
    Boolean existsByToken(String token);
}