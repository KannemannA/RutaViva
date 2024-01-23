package com.bookingProject.tour.exp.repository;

import com.bookingProject.tour.exp.entity.Booking;
import com.bookingProject.tour.exp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.product.id = :id")
    List<Booking> findByIdProduct(Long id);
    List<Booking> findByIdGreaterThan(Long id);
}
