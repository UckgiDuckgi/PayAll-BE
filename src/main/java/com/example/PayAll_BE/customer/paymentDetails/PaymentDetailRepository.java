package com.example.PayAll_BE.customer.paymentDetails;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.PayAll_BE.customer.payment.Payment;

@Repository
public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, Long> {
	List<PaymentDetail> findByPaymentId(Long paymentId);

	// 최근 결제 상품 중 조회
	@Query("SELECT pd FROM PaymentDetail pd " +
		"JOIN pd.payment p " +
		"JOIN p.account a " +
		"WHERE p.category = 'SHOPPING' AND NOT p.paymentType = 'OFFLINE' " +
		"AND a.user.id = :userId " +
		"ORDER BY p.paymentTime DESC")
	List<PaymentDetail> findRecentPaymentsByUserId(@Param("userId") Long userId, Pageable pageable);

	List<PaymentDetail> findByPayment(Payment payment);
}
