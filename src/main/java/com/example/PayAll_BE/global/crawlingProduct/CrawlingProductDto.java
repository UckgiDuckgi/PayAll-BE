package com.example.PayAll_BE.global.crawlingProduct;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrawlingProductDto {
	private Long pCode;
	private String productName;
	private String productImage;
	private Long price;
	private String shopName;
	private String shopUrl;
}
