package com.example.PayAll_BE.customer.purchase;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseRequestDto {
	private List<PurchaseProductDto> purchaseList;
	private Long totalPrice;
	private Long totalDiscountPrice;

	@Data
	@Builder
	public static class PurchaseProductDto {
		private Long cartId;
		private Long productId;
		private String productName;
		private Long productPrice;
		private String storeName;
		private int quantity;
	}
}
