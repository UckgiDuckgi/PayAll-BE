package com.example.PayAll_BE.dto.Purchase;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseRequestDto {
	private List<PurchaseProductDto> purchaseList;
	private Long totalPrice;

	@Data
	public static class PurchaseProductDto {
		private Long productId;
		private String productName;
		private Long productPrice;
		private int quantity;
	}
}
