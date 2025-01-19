package com.example.PayAll_BE.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.PayAll_BE.entity.Benefit;

public interface BenefitRepository extends JpaRepository<Benefit, Long> {
	@Query("SELECT b FROM Benefit b JOIN Store s ON b.store.id = s.id WHERE s.storeName = :paymentPlace ORDER BY b.benefitValue DESC LIMIT 1")
	Optional<Benefit> findByPaymentPlace (String paymentPlace);

	@Query("SELECT b, s FROM Benefit b JOIN b.store s WHERE b.product.id = :productId")
	List<Object[]> findBenefitsWithStoresByProductId(@Param("productId") Long productId);
}
