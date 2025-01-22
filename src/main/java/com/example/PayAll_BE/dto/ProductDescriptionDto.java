package com.example.PayAll_BE.dto;

import com.example.PayAll_BE.entity.enums.Category;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDescriptionDto {
	@JsonProperty("product_name")
	private String productName;

	@JsonProperty("product_description")
	private String productDescription;

	@JsonProperty("benefit_description")
	private String benefitDescription;
}
