package com.example.PayAll_BE.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponseDto {
	private Long cartId;
	private Long productId;
	private String productName;
	private String productImage;
	private Long price;
	private int quantity;
	private String store;
	private String link;
}
