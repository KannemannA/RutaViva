package com.bookingProject.tour.exp.repository;

import com.bookingProject.tour.exp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product,Long> {
    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.id = :categoryId")
    List<Product> findByCategoryId(Long categoryId);
    @Query("SELECT p FROM Product p JOIN p.characteristics c WHERE c.id = :characterId")
    List<Product> findByCharacterId(Long characterId);
}
