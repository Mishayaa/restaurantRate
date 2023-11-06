package com.example.restaurantestimate.services;

import com.example.restaurantestimate.entities.Role;
import com.example.restaurantestimate.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole() {
        Role role = new Role("ROLE_USER");
        return roleRepository.save(role);
    }
}