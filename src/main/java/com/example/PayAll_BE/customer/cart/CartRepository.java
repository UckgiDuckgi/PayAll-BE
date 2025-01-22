package com.example.PayAll_BE.customer.cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByUserIdAndProductId(Long userId, Long productId);

	List<Cart> findAllByUserId(Long userId);
}
