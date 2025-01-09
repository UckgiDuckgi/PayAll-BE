package com.example.PayAll_BE.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface CardBenefitsRepository extends JpaRepository<CardBenefits, Long> {
	CardBenefits findTopByStoreNameOrderByBenefitValueDesc(String merchantName);
}
