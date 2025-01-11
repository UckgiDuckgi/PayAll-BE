package com.example.PayAll_BE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.PayAll_BE.entity.CardBenefit;

@Repository
public interface CardBenefitsRepository extends JpaRepository<CardBenefit, Long> {
	CardBenefit findTopByStoreNameOrderByBenefitValueDesc(String merchantName);
}
