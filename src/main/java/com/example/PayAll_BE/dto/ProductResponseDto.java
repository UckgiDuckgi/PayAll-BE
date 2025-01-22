package com.example.PayAll_BE.dto;

import java.util.List;

import com.example.PayAll_BE.entity.enums.Category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDto {
	private String productName;
	private String benefitDescription;
	private List<StoreDetailDto> storeDetails;

	@Data
	@Builder
	public static class StoreDetailDto {
		private Category category;
		private String storeName;
		private Long discountAmount;
		private int visitCount;
	}
}
