package com.example.PayAll_BE.entity;

import java.math.BigInteger;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long paymentId;

	private BigInteger price;

	private LocalDateTime paymentTime;

	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;

	@Enumerated(EnumType.STRING)
	private Category category;

	private String paymentPlace;

	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;

	//todo @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
	//private List<PaymentDetail> paymentDetails;

	public enum PaymentType {
		CREDIT, DEBIT, CASH
	}

	public enum Category {
		FOOD, TRANSPORT, ENTERTAINMENT, OTHER
	}
}
