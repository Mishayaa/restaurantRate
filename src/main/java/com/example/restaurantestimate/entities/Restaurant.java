package com.example.restaurantestimate.entities;


import com.example.restaurantestimate.dto.google.Geometry;
import com.example.restaurantestimate.dto.google.OpeningHours;
import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
;

import javax.persistence.*;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@ToString
@NoArgsConstructor
@Table(name = "restaurants")
@Indexed
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @FullTextField(name = "name")
    private String name;

    @OneToMany(mappedBy = "restaurant")
    private List<Review> reviews;
    @DecimalMin(value = "0.0", message = "Rating can't be less than 0.0")
    @DecimalMax(value = "5.0", message = "Rating can't be more than 5.0")
    private Double rating;

    @Column(name = "poster_url",length = 1000)
    private String posterUrl;
    private String formatted_address;
    private Boolean open_now;
}
