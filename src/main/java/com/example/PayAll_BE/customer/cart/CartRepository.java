package com.example.PayAll_BE.customer.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
	Optional<Cart> findByUserIdAndProductIdAndProductPriceAndStoreName(Long userId, Long productId, Long productPrice,
		String storeName);

	List<Cart> findAllByUserId(Long userId);
}
