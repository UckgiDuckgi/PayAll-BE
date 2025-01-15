package com.example.PayAll_BE.mapper;

import com.example.PayAll_BE.dto.PaymentDetail.PaymentDetailDto;
import com.example.PayAll_BE.dto.ProductDto;
import com.example.PayAll_BE.entity.PaymentDetail;

public class PaymentDetailMapper {

	public static PaymentDetailDto toDto(PaymentDetail paymentDetail, ProductDto productDto) {
		return PaymentDetailDto.builder()
			.paymentDetailId(paymentDetail.getId())
			.productName(paymentDetail.getProductName())
			.price(paymentDetail.getProductPrice())
			.quantity(paymentDetail.getQuantity())
			.lowestPricePlace(productDto.getShopName())
			.lowestPrice(productDto.getPrice())
			.link(productDto.getShopUrl())
			.build();
	}
}
