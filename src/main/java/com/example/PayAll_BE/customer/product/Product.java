package com.example.PayAll_BE.customer.product;

import java.util.ArrayList;
import java.util.List;

import com.example.PayAll_BE.customer.benefit.Benefit;
import com.example.PayAll_BE.customer.enums.ProductType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long id;

	@Column(name = "product_name")
	private String productName;

	@Column(name = "product_description")
	private String productDescription;

	@Column(name = "benefit_description")
	private String benefitDescription;

	@Column(name = "product_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private ProductType productType;

	@OneToMany(mappedBy = "product")
	@Builder.Default
	private List<Benefit> benefits = new ArrayList<>();
}
