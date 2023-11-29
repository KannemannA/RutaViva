package com.bookingProject.tour.exp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Rating {
    @Id
    @SequenceGenerator(name = "rating_secuence",sequenceName = "rating_secuence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "rating_secuence")
    private Long id;
    private int star;
    private LocalDate date;
    @Column(length = 2000)
    private String description;
    /*private UserEntity user;
    private Product product;*/
}
