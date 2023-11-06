package com.example.restaurantestimate.entities;


import lombok.*;

import javax.persistence.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "name")
    private String name;

    public Role(String name) {
        this.name = name;
    }
}