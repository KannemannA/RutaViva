package com.bookingProject.tour.exp.repository;

import com.bookingProject.tour.exp.entity.Politic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPoliticRepository extends JpaRepository<Politic,Long> {
    Optional<Politic> findByTitle(String title);
    List<Politic> findByIdGreaterThan(Long id);
}
