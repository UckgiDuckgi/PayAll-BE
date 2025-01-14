package com.example.PayAll_BE.dto;

import lombok.Data;

@Data
public class ProductDto {
	private Long pCode;
	private String productName;
	private String productImage;
	private Long price;
	private String shopName;
	private String shopUrl;
}
