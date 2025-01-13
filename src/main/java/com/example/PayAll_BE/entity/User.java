package com.example.PayAll_BE.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId; // Primary Key

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "auth_id", nullable = false, unique = true)
	private String authId;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "phone", length = 20)
	private String phone;

	@Column(name = "address", length = 255)
	private String address;
}
