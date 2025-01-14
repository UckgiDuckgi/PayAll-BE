package com.example.PayAll_BE.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "User")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String authId;

	@Column(nullable = false)
	private String password;

	private String phone;
	private String address;

	// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	// private List<Cart> carts = new ArrayList<>();

}
