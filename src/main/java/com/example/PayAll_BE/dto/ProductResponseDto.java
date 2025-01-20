package com.example.PayAll_BE.dto;

import com.example.PayAll_BE.entity.enums.Category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDto {
	private String productName;
	private String benefitDescription;
	private Category category;
	private String storeName;
	private Long discountAmount;
	private int visitCount;
}
