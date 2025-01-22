package com.example.PayAll_BE.customer.cart.dto;

import com.example.PayAll_BE.customer.cart.Cart;

public class CartMapper {
	public static CartResponseDto toDto(Cart cart) {
		return CartResponseDto.builder()
			.cartId(cart.getCartId())
			.productId(cart.getProductId())
			.productName(cart.getProductName())
			.image(cart.getImage())
			.productPrice(cart.getProductPrice())
			.quantity(cart.getQuantity())
			.storeName(cart.getStoreName())
			.link(cart.getLink())
			.prevPrice(cart.getPrevPrice())
			.build();

	}
}
