package com.example.restaurantestimate.entities;


import lombok.*;

import javax.persistence.*;

import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy="restaurant")
    private List<Review> reviews;

}
