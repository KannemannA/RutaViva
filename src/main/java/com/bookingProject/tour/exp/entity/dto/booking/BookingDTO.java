package com.bookingProject.tour.exp.entity.dto.booking;

import com.bookingProject.tour.exp.entity.Product;
import com.bookingProject.tour.exp.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private Long id;
    @NotBlank
    private LocalDate checkIn;
    @NotBlank
    private LocalDate checkOut;
    @NotBlank
    private Long userId;
    @NotBlank
    private Long productId;
}
