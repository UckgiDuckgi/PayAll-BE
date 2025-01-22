package com.example.PayAll_BE.customer.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDescriptionDto {
	private Long productId;
	private String productName;
	private String productDescription;
	private String benefitDescription;
}
