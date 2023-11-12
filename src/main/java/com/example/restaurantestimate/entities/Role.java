package com.example.restaurantestimate.entities;


import lombok.*;

import javax.persistence.*;


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