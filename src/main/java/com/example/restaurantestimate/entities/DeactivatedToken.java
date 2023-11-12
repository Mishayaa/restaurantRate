package com.example.restaurantestimate.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

import java.util.Date;


@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Table(name = "deactivated_tokens")
public class DeactivatedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date timestamp;

    public DeactivatedToken(String tokjavaxen, Date timestamp) {
        this.token = token;
        this.timestamp = timestamp;
    }
}
