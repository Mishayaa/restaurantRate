package com.example.restaurantestimate.repositories;

import com.example.restaurantestimate.entities.DeactivatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeactivatedTokenRepository extends JpaRepository<DeactivatedToken, Long> {
    Boolean existsByToken(String token);
}