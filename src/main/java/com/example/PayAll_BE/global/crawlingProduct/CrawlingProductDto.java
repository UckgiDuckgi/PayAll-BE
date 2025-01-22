package com.example.PayAll_BE.global.crawlingProduct;

import lombok.Data;

@Data
public class CrawlingProductDto {
	private Long pCode;
	private String productName;
	private String productImage;
	private Long price;
	private String shopName;
	private String shopUrl;
}
