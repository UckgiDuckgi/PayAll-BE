package com.example.PayAll_BE.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.PayAll_BE.entity.enums.Category;

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
	private Long id;

	private String storeName;

	@Enumerated(EnumType.STRING)
	private Category category;

	@OneToMany(mappedBy = "store")
	private List<Benefit> benefits = new ArrayList<>();
}
