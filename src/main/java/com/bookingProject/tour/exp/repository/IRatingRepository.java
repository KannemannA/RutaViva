package com.bookingProject.tour.exp.repository;

import com.bookingProject.tour.exp.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRatingRepository extends JpaRepository<Rating,Long> {
}
