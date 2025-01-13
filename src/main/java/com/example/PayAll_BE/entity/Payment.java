package com.example.PayAll_BE.entity;

import java.time.LocalDateTime;

import com.example.PayAll_BE.entity.enums.Category;
import com.example.PayAll_BE.entity.enums.PaymentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "Payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	@Column(length = 300, nullable = false)
	private String paymentPlace;

	@Column(nullable = false)
	private Long price;

	@Column(nullable = false)
	private LocalDateTime paymentTime;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Category category;

	@Builder
	public Payment(Account account, String paymentPlace, Long price, LocalDateTime paymentTime, PaymentType paymentType,
		Category category) {
		this.account = account;
		this.paymentPlace = paymentPlace;
		this.price = price;
		this.paymentTime = paymentTime;
		this.paymentType = paymentType;
		this.category = category;
	}
}
