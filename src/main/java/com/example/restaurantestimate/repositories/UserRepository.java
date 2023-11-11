package com.example.restaurantestimate.repositories;

import com.example.restaurantestimate.entities.Restaurant;
import com.example.restaurantestimate.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    @Query(value = "SELECT u.favorites FROM User u WHERE u.id = :id")
    Page<Restaurant> getFavorites(Long id, Pageable pageable);
}