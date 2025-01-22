package com.example.PayAll_BE.mapper;

import com.example.PayAll_BE.customer.product.dto.ProductDescriptionDto;
import com.example.PayAll_BE.customer.product.Product;

public class ProductMapper {

	public static ProductDescriptionDto toDto(Product product) {
		return ProductDescriptionDto.builder()
			.productName(product.getProductName())
			.productDescription(product.getProductDescription())
			.benefitDescription(product.getBenefitDescription())
			.build();
	}
}
