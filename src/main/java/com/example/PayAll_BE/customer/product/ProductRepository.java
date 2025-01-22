package com.example.PayAll_BE.customer.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.PayAll_BE.customer.product.Product;

public interface ProductRepository  extends JpaRepository<Product,Long> {

	@Query("SELECT p FROM Product p WHERE p.id = :productId")
	Optional<Product> findById(@Param("productId") Long productId);


	@Query("SELECT p FROM Product p WHERE p.productType = 'CARD'")
	List<Product> findAllCards();

	@Query("SELECT p FROM Product p WHERE p.productType = 'SUBSCRIBE'")
	List<Product> findAllSubscribes();
}
