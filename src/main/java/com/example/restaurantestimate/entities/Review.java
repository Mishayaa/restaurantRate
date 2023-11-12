package com.example.restaurantestimate.entities;

import lombok.*;

import javax.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

@Builder
@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(name = "userReview", columnDefinition = "TEXT")
    @Size(max = 1000, message = "review is too big.")
    private String userReview;

    @DecimalMin(value = "0.0", message = "Rating can't be less than 0.0")
    @DecimalMax(value = "5.0", message = "Rating can't be more than 5.0")
    private Double rating;

}