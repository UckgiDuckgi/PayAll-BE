package com.example.PayAll_BE.customer.paymentDetails.dto;

import com.example.PayAll_BE.customer.paymentDetails.PaymentDetail;
import com.example.PayAll_BE.global.crawlingProduct.CrawlingProductDto;

public class PaymentDetailMapper {

	public static PaymentDetailDto toDto(PaymentDetail paymentDetail, CrawlingProductDto crawlingProductDto) {
		return PaymentDetailDto.builder()
			.paymentDetailId(paymentDetail.getId())
			.productName(paymentDetail.getProductName())
			.price(paymentDetail.getProductPrice())
			.quantity(paymentDetail.getQuantity())
			.lowestPricePlace(crawlingProductDto.getShopName())
			.lowestPrice(crawlingProductDto.getPrice())
			.link(crawlingProductDto.getShopUrl())
			.productId(crawlingProductDto.getPCode())
			.build();
	}

	public static PaymentDetailDto toDto(PaymentDetail paymentDetail) {
		return PaymentDetailDto.builder()
			.paymentDetailId(paymentDetail.getId())
			.productName(paymentDetail.getProductName())
			.price(paymentDetail.getProductPrice())
			.quantity(paymentDetail.getQuantity())
			.lowestPricePlace(null)
			.lowestPrice(null)
			.link(null)
			.productId(null)
			.build();
	}
}
