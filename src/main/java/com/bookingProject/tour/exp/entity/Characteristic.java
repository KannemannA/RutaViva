package com.bookingProject.tour.exp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "characteristic")
public class Characteristic {
    @Id
    @SequenceGenerator(name = "character_secuence",sequenceName = "character_secuence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "character_secuence")
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(length = 1000)
    private String icon;
}
