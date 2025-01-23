package com.example.PayAll_BE.customer.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartRequestDto {
	private Long productId;
	private String productName;
	private String productImage;
	private String shopName;
	private String shopUrl;
	private Long price;
	private int quantity;
	private Long prevPrice;
	private boolean search;

}
