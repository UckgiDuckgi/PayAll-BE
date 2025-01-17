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

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;

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

	// @Query(value = "SELECT " +
	// 	"p.category as category, " +
	// 	"SUM(p.price) as total_spent, " +
	// 	"p.payment_place as paymentPlace, " +
	// 	"SUM(p.price) as storeTotalSpent, " +
	// 	"COUNT(*) as storePurchaseCount " +
	// 	"FROM payment p " +
	// 	"WHERE p.account_id IN (" +
	// 	"SELECT a.account_id " +
	// 	"FROM account a " +
	// 	"WHERE a.user_id = :userId " +
	// 	") " +
	// 	"GROUP BY p.category, p.payment_place " +
	// 	"ORDER BY p.category, p.payment_place",
	// 	nativeQuery = true)
// 	@Repository
// 	public interface PaymentRepository extends JpaRepository<Payment, Long> {
//
// 		@Query(value = "SELECT " +
// 			"p.category AS name, " +
// 			"p.payment_place AS store, " +
// 			"SUM(p.price) AS total_spent, " +
// 			"CASE " +
// 			"   WHEN p.payment_place IS NULL THEN 'CATEGORY' " +
// 			"   ELSE 'STORE' " +
// 			"END AS type " +
// 			"FROM payment p " +
// 			"JOIN account a ON p.account_id = a.account_id " +
// 			"WHERE a.user_id = :userId " +
// 			"GROUP BY p.category, p.payment_place WITH ROLLUP " +
// 			"ORDER BY name, store",
// 			nativeQuery = true)
// 		List<StoreStatisticsDto> getCategoryStoreStats(@Param("userId") Long userId);
// 	}
// 	List<StoreStatisticsDto> getCategoryStoreStats(@Param("userId") Long userId);
//
// }


	@Query(value = "SELECT " +
		"CASE " +
		"   WHEN p.category IS NULL THEN 'TOTAL' " +
		"   ELSE p.category " +
		"END AS name, " +
		"CASE " +
		"   WHEN p.payment_place IS NULL THEN 'ALL_STORES' " +
		"   ELSE p.payment_place " +
		"END AS store, " +
		"CAST(SUM(p.price) AS SIGNED) AS total_spent, " +
		"COUNT(*) AS storePurchaseCount, " +
		"CASE " +
		"   WHEN p.payment_place IS NULL THEN 'CATEGORY' " +
		"   ELSE 'STORE' " +
		"END AS type " +
		"FROM payment p " +
		"JOIN account a ON p.account_id = a.account_id " +
		"WHERE a.user_id = ?1 " +  // 위치 기반 파라미터로 변경
		"AND p.payment_time BETWEEN ?2 AND ?3 " +  // 기간 조건 추가
		"GROUP BY p.category, p.payment_place WITH ROLLUP " +
		"ORDER BY name, store",
		nativeQuery = true)
	List<StoreStatisticsDto> getCategoryStoreStats(@Param("userId") Long userId,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate);
	}
