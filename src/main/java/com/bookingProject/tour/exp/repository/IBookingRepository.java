package com.bookingProject.tour.exp.repository;

import com.bookingProject.tour.exp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBookingRepository extends JpaRepository<Booking, Long> {
}
