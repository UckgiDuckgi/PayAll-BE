package com.example.PayAll_BE.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.PayAll_BE.dto.StoreStatisticsDto;
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

	@Query(value = "SELECT " +
		"p.category as category, " +
		"SUM(p.price) as total_spent, " +
		"p.payment_place as paymentPlace, " +
		"SUM(p.price) as storeTotalSpent, " +
		"COUNT(*) as storePurchaseCount " +
		"FROM payment p " +
		"WHERE p.account.id IN (" +
		"    SELECT a.id " +
		"    FROM Account a " +
		"    WHERE a.user.id = :userId " +
		") " +
		"GROUP BY p.category, p.payment_place " +
		"ORDER BY p.category, p.payment_place",
		nativeQuery = true)
	List<StoreStatisticsDto> getCategoryStoreStats(@Param("userId") Long authId);

}
