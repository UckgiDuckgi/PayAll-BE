package com.example.PayAll_BE.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "Payment_Detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class PaymentDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_detail_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id", nullable = false)
	private Payment payment;

	@Column(name = "product_id")
	private Long productId;

	@Column(nullable = false, name = "product_name")
	private String productName;

	@Column(nullable = false, name = "product_price")
	private Long productPrice;

	@Column(nullable = false)
	private int quantity;

	@Builder
	public PaymentDetail(Payment payment, String productName, Long productPrice, int quantity, String productId) {
		this.payment = payment;
		this.productName = productName;
		this.productPrice = productPrice;
		this.quantity = quantity;
		this.productId = productId;
	}
}
