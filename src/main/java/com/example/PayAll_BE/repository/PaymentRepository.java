package com.example.PayAll_BE.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.PayAll_BE.entity.Payment;
import com.example.PayAll_BE.entity.enums.StatisticsCategory;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	// 최근 결제 상품 중 조회
	@Query("SELECT p FROM Payment p " +
		"JOIN FETCH p.paymentDetails pd " +
		"WHERE p.account.id IN (SELECT a.id FROM Account a WHERE a.user.id = :userId) " +
		"ORDER BY p.paymentTime DESC")
	List<Payment> findRecentPaymentsByUserId(@Param("userId") Long userId, Pageable pageable);

	// List<Payment> findByAccountId(Long accountId);
	List<Payment> findByAccount_User_IdAndCategoryAndPaymentTimeBetween(
		Long userId,
		StatisticsCategory category,
		LocalDateTime startDate,
		LocalDateTime endDate
	);

}
