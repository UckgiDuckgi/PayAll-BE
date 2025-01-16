package com.example.PayAll_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.PayAll_BE.entity.Product;

public interface CardRepository extends JpaRepository<Product,Long> {

	@Query("SELECT c FROM Product c WHERE c.cardName = :category")
	Optional<Product> findByCategory(@Param("category") String category);
}
