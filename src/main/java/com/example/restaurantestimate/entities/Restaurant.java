package com.example.restaurantestimate.entities;


import com.querydsl.core.annotations.QueryEntity;
import lombok.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Table(name = "restaurants")
@Indexed
public class Restaurant {

    @Id
    @GeneratedValue
    private Long id;
    @FullTextField(name = "name")
    private String name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @OneToMany(mappedBy = "restaurant")
    private List<Review> reviews;
    @DecimalMin(value = "0.0", message = "Rating can't be less than 0.0")
    @DecimalMax(value = "5.0", message = "Rating can't be more than 5.0")
    private Double rating;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "restaurant_cuisines",
            joinColumns = @JoinColumn(name = "restaurants_id"),
            inverseJoinColumns = @JoinColumn(name = "cuisines_id")
    )
    private Set<Cuisine> cuisines;
    @Column(name = "poster_url")
    private String posterUrl;
}
