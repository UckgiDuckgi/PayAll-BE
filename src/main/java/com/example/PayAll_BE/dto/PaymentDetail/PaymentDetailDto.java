package com.example.PayAll_BE.dto.PaymentDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetailDto {
	private String productName;        // 상품명
	private Long price;                // 상품 가격
	private Long lowestPrice;          // 최저가
	private String lowestPricePlace;   // 최저가 쇼핑몰
	private String link;               // 구매 링크
}
