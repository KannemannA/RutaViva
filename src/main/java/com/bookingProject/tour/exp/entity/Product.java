package com.bookingProject.tour.exp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "product")
public class Product {
    @Id
    @SequenceGenerator(name = "product_secuence",sequenceName = "product_secuence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "product_secuence")
    private Long id;
    @Column(unique = true)
    private String title;
    @Column(length = 1000)
    private String description;
    @Column(length = 1000)
    private String thumbnail;
    @Column(length = 4000)
    private List<String> images;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_category",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Category category;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "product_characteristic",
            joinColumns = @JoinColumn(name = "id_product",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)),
            inverseJoinColumns = @JoinColumn(name = "id_characteristic",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)))
    private List<Characteristic> characteristics;
    @Column(length = 30000)
    private Set<LocalDate> bookings;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product")
    private List<Politic> politics;
    /*@OneToMany(fetch = FetchType.EAGER)
    private List<Rating> rating;*/
}
