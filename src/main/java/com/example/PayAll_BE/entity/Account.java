package com.example.PayAll_BE.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Table(name = "Account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
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
