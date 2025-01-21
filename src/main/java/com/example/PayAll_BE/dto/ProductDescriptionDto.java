package com.example.PayAll_BE.dto;

import com.example.PayAll_BE.entity.enums.Category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDescriptionDto {
	private String productName;
	private String productDescription;
	private String benefitDescription;
}
