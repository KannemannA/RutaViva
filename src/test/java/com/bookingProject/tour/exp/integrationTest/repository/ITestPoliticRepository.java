package com.bookingProject.tour.exp.integrationTest.repository;

import com.bookingProject.tour.exp.entity.Politic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITestPoliticRepository extends JpaRepository<Politic,Long> {
}
