package com.example.restaurantestimate.entities;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    @Email(message = "Email должен соотвестновать паттерну *@*.*")
    private String email;

    @Column(name = "username", unique = true, nullable = false)
    @NotBlank(message = "Введите логин пользователя чтобы зарегистрироваться")
    private String username;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Введите пароль чтобы зарегистрироваться")
    private String password;
    @Column(name = "about", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Описание слишком большое, краткость сестра таланта.")
    private String about;
    @OneToMany(mappedBy = "user")
    private List<Review> reviews;
    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;
    @OneToMany
    private Set<Restaurant> favorites = new HashSet<>();


}