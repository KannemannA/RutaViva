package com.bookingProject.tour.exp.repository;

import com.bookingProject.tour.exp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICategoryRepository extends JpaRepository<Category,Long> {
    Optional<Category> findByCategory(String category);
}
