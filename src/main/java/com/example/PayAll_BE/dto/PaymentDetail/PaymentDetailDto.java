package com.example.PayAll_BE.dto.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailDto {
	private Long paymentDetailId;
	private String productName;
	private Long price;
	private int quantity;
	private String lowestPricePlace;  // 최저가 쇼핑몰 이름
	private Long lowestPrice;         // 최저가 금액
	private String link;              // 최저가 상품 링크
}
