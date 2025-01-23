package com.example.PayAll_BE.customer.recommendation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommendProductDto {
	private Long productId;
	private String productName;
	private String productImage;
	private Long price;
	private String storeName;
	private String link;
	private Double discountRate;

}
