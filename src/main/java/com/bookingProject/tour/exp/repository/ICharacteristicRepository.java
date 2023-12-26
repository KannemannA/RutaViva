package com.bookingProject.tour.exp.repository;

import com.bookingProject.tour.exp.entity.Characteristic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICharacteristicRepository extends JpaRepository<Characteristic, Long> {
    Optional<Characteristic> findByName(String name);
}
