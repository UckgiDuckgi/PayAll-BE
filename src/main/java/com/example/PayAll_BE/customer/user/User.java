package com.example.PayAll_BE.customer.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "User")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(name = "auth_id", nullable = false)
	private String authId;

	@Column(nullable = false)
	private String password;

	private String phone;
	private String address;
	private String email;

	@Column(name = "coupang_id")
	private String coupangId;

	@Column(name = "coupang_password")
	private String coupangPassword;

	@Column(name = "elevenst_id")
	private String elevenstId;

	@Column(name = "elevenst_password")
	private String elevenstPassword;

	@Column(name = "naver_id")
	private String naverId;

	@Column(name = "naver_password")
	private String naverPassword;

	@Column(name = "permission", nullable = false)
	private boolean permission = false;
}
