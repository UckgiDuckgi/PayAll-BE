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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "Account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(length = 50, nullable = false)
	private String bankName;

	@Column(length = 50, nullable = false)
	private String accountName;

	@Column(length = 30, unique = true, nullable = false)
	private String accountNumber;

	@Column(nullable = false)
	private Long balance;

	@Builder
	public Account(User user, String bankName, String accountName, String accountNumber, Long balance) {
		this.user = user;
		this.bankName = bankName;
		this.accountName = accountName;
		this.accountNumber = accountNumber;
		this.balance = balance;
	}
}
