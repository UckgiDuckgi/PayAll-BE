package com.example.PayAll_BE.dto.Cart;

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
	private String image;
	private Long productPrice;
	private int quantity;
	private String storeName;
	private String link;
	private Long prevPrice;
}
