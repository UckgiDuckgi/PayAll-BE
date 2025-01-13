package com.example.PayAll_BE.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

import com.example.PayAll_BE.entity.enums.Category;
import com.example.PayAll_BE.entity.enums.PaymentType;

@Entity
@Getter
@Table(name = "Payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
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
	public Payment(Account account, String paymentPlace, Long price, LocalDateTime paymentTime, PaymentType paymentType, Category category) {
		this.account = account;
		this.paymentPlace = paymentPlace;
		this.price = price;
		this.paymentTime = paymentTime;
		this.paymentType = paymentType;
		this.category = category;
	}
}
