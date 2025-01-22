package com.example.PayAll_BE.customer.product.dto;

import com.example.PayAll_BE.customer.product.Product;

public class ProductMapper {

	public static ProductDescriptionDto toDto(Product product) {
		return ProductDescriptionDto.builder()
			.productId(product.getId())
			.productName(product.getProductName())
			.productDescription(product.getProductDescription())
			.benefitDescription(product.getBenefitDescription())
			.build();
	}
}
