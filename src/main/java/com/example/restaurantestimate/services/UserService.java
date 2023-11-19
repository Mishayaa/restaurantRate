package com.example.restaurantestimate.services;


import com.example.restaurantestimate.dto.user.UserDtoAboutRequest;
import com.example.restaurantestimate.dto.user.UserDtoResponse;
import com.example.restaurantestimate.entities.Restaurant;
import com.example.restaurantestimate.entities.User;
import com.example.restaurantestimate.exceptions.CustomAccessDeniedException;
import com.example.restaurantestimate.mappers.UserSerializer;
import com.example.restaurantestimate.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserSerializer userSerializer;


    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь с таким именем '%s' не найден", username)
        ));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с таким ID '%s' не найден", id)
        ));
    }

    public UserDtoResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с данным ID не найден."));

        return userSerializer.apply(user);
    }

    @Transactional
    public Page<Restaurant> getUserFavorites(Long userId, PageRequest pageRequest) {
        return userRepository.getFavorites(userId, pageRequest);
    }

    public boolean isFavoriteMovieForCurrentUser(Long userId, Long movieId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getFavorites().stream()
                .anyMatch(e -> e.getId().equals(movieId));
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList()
        );
    }


    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            return null;
        }

    }

    public Optional<User> getCurrentUserOptional() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username);


    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public UserDtoResponse updateAbout(Long userId, UserDtoAboutRequest aboutRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с данным ID не найден."));
        User currentUser = getCurrentUser();
        if (!user.getId().equals(currentUser.getId())) {
            throw new CustomAccessDeniedException("Нельзя обновить данные другого пользователя!");
        }

        user.setId(userId);
        user.setAbout(aboutRequest.getAbout());
        userRepository.save(user);

        return userSerializer.apply(user);
    }
}