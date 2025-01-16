package com.example.PayAll_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.PayAll_BE.entity.Product;

public interface ProductRepository  extends JpaRepository<Product,Long> {

	@Query("SELECT p FROM Product p WHERE p.id = :productId")
	Optional<Product> findById(@Param("productId") Long productId);
}
