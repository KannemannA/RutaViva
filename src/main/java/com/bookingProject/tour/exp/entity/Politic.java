package com.bookingProject.tour.exp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "politic")
public class Politic {
    @Id
    @SequenceGenerator(name = "politic_secuence", sequenceName = "politic_secuence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "politic_secuence")
    private Long id;
    private String title;
    @Column(length = 1000)
    private String description;
    @ManyToOne
    @JoinColumn(name = "id_product",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    private Product product;
}
