package com.example.PayAll_BE.customer.store;

import java.util.ArrayList;
import java.util.List;

import com.example.PayAll_BE.customer.benefit.Benefit;
import com.example.PayAll_BE.customer.enums.Category;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "store_id")
	private Long id;

	private String storeName;

	@Enumerated(EnumType.STRING)
	private Category category;

	@OneToMany(mappedBy = "store")
	@Builder.Default
	private List<Benefit> benefits = new ArrayList<>();
}
