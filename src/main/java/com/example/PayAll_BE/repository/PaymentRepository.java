package com.example.PayAll_BE.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.PayAll_BE.dto.StoreStatisticsDto;
import com.example.PayAll_BE.entity.Payment;
import com.example.PayAll_BE.entity.enums.Category;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	@Query("SELECT p FROM Payment p WHERE p.account.user.id = :userId " +
		"AND (:category IS NULL OR p.category = :category) " +
		"ORDER BY p.paymentTime DESC")
	Page<Payment> findAllByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category, Pageable pageable);


	// 최근 결제 상품 중 조회
	@Query("SELECT p FROM Payment p " +
		"JOIN FETCH p.paymentDetails pd " +
		"WHERE p.account.id IN (SELECT a.id FROM Account a WHERE a.user.id = :userId) " +
		"ORDER BY p.paymentTime DESC")
	List<Payment> findRecentPaymentsByUserId(@Param("userId") Long userId, Pageable pageable);

	Payment findByAccount_User_IdAndPaymentTimeAndPaymentPlace(
		Long userId, LocalDateTime paymentTime, String paymentPlace
	);
	// List<Payment> findByAccountId(Long accountId);
	List<Payment> findByAccount_User_IdAndCategoryAndPaymentTimeBetween(
		Long userId, Category category, LocalDateTime startDate, LocalDateTime endDate
	);

	// 특정 결제처와 시간대에 해당하는 결제 내역 가져오기
	@Query("SELECT p FROM Payment p WHERE p.account.id = :accountId AND p.paymentTime = :paymentTime AND p.paymentPlace IN ('카카오페이', '네이버페이')")
	Payment findPaymentToUpdateByAccountIdAndPaymentTime(Long accountId, LocalDateTime paymentTime);
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


	// 결제 내역의 실제 결제처 업데이트
	@Transactional
	@Modifying
	@Query("UPDATE Payment p SET p.paymentPlace = :newPaymentPlace WHERE p.id = :paymentId ")
	int updatePaymentPlace(@Param("paymentId") Long paymentId, @Param("newPaymentPlace") String newPaymentPlace);
}
