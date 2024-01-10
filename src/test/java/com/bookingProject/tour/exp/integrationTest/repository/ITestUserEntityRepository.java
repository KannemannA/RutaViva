package com.bookingProject.tour.exp.integrationTest.repository;

import com.bookingProject.tour.exp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITestUserEntityRepository extends JpaRepository<UserEntity,Long> {
}
