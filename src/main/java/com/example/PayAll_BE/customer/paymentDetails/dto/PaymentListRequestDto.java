package com.example.PayAll_BE.customer.paymentDetails.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentListRequestDto{
	private List<PaymentDetailInfoRequestDto> paymentList;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class PaymentDetailInfoRequestDto {
		private Long paymentTime;
		private String paymentPlace;
		private List<PurchaseProductRequestDto> purchaseProductList;

		@Data
		@AllArgsConstructor
		@NoArgsConstructor
		@Builder
		public static class PurchaseProductRequestDto {
			private String productName;
			private Long price;
			private int quantity;
		}
	}
}
