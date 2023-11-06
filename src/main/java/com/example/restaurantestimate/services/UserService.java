package com.example.restaurantestimate.services;

import com.example.restaurantestimate.configs.EncoderConfig;
import com.example.restaurantestimate.dto.UserRegistrationDtoRequest;
import com.example.restaurantestimate.dto.UserDtoResponse;
import com.example.restaurantestimate.entities.User;
import com.example.restaurantestimate.exceptions.EntityAlreadyExistException;
import com.example.restaurantestimate.exceptions.PasswordException;
import com.example.restaurantestimate.mappers.UserSerializer;
import com.example.restaurantestimate.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private EncoderConfig encoderConfig;
    private final UserSerializer userSerializer;

    @Autowired
    public void setEncoderConfig(EncoderConfig encoderConfig) {
        this.encoderConfig = encoderConfig;
    }

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

    @Transactional
    public UserDtoResponse createUser(UserRegistrationDtoRequest authRegistrationRequest) {
        Optional<User> userInDbWithUsername = userRepository.findByUsername(authRegistrationRequest.getUsername());
        Optional<User> userInDbWithEmail = userRepository.findByEmail(authRegistrationRequest.getEmail());
        if (userInDbWithUsername.isPresent() || userInDbWithEmail.isPresent()) {
            throw new EntityAlreadyExistException("Пользователь с таким именем или email уже существует");
        }
        if (!Objects.equals(authRegistrationRequest.getPassword(), authRegistrationRequest.getConfirmPassword())) {
            throw new PasswordException("Пароль и потверждающий пароль не совпадают");
        }
        User user = new User();
        user.setEmail(authRegistrationRequest.getEmail());
        user.setUsername(authRegistrationRequest.getUsername());
        user.setPassword(encoderConfig.passwordEncoder().encode(authRegistrationRequest.getPassword()));
        user.setRoles(List.of(roleService.getUserRole()));
        userRepository.save(user);

        return userSerializer.apply(user);
    }
}