package com.example.PayAll_BE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.PayAll_BE.entity.Benefit;

@Repository
public interface CardBenefitRepository extends JpaRepository<Benefit, Long> {
	Benefit findTopByStoreNameOrderByBenefitValueDesc(String merchantName);
}
