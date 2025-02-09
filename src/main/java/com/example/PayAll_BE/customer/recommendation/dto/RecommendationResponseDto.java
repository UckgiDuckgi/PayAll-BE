package com.example.PayAll_BE.customer.recommendation.dto;

import com.example.PayAll_BE.customer.enums.Category;
import com.example.PayAll_BE.customer.enums.ProductType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationResponseDto {
	private String storeName;
	private Long discountAmount;
	private Category category;
	private Long productId;
	private String productName;
	private ProductType productType;
}
