package com.bookingProject.tour.exp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category")
public class Category {
    @Id
    @SequenceGenerator(name = "category_secuence",sequenceName = "category_secuence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "category_secuence")
    private Long id;
    @Column(unique = true)
    private String category;
    @Column(length = 1000)
    private String description;
    @Column(length = 1000)
    private String thumbnail;
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Product> products;
}
