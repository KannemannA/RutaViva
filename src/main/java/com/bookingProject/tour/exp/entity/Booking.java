package com.bookingProject.tour.exp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Booking {
    @Id
    @SequenceGenerator(name = "booking_secuence",sequenceName = "booking_secuence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "booking_secuence")
    private Long id;
    private List<LocalDate> reservedDate;
    @ManyToOne
    @JoinColumn(name = "id_user",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "id_product",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    private Product product;
}
