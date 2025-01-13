package com.example.PayAll_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long accountId;

	@Column(nullable = false)
	private Integer userId;

	@Column(nullable = false, length = 100)
	private String bankName;

	@Column(nullable = false, length = 100)
	private String accountName;

	@Column(nullable = false, length = 50, unique = true)
	private String accountNumber;

	@Column(nullable = false)
	private Long balance;
}
