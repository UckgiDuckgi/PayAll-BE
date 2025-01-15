package com.example.PayAll_BE.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.PayAll_BE.entity.Payment;
import com.example.PayAll_BE.entity.enums.StatisticsCategory;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	// List<Payment> findByAccountId(Long accountId);
	List<Payment> findByAccount_User_IdAndCategoryAndPaymentTimeBetween(
		Long userId,
		StatisticsCategory category,
		LocalDateTime startDate,
		LocalDateTime endDate
	);

	List<Payment> findByAccountId(Long accountId);

}
