package com.example.PayAll_BE.customer.receipt.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiptRequestDto {
	private Long paymentId;
	private List<ReceiptDetailDto> productList;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class ReceiptDetailDto {
		private String productName;
		private int quantity;
		private Long price;

	}
}
