package com.example.PayAll_BE.mapper;

import com.example.PayAll_BE.dto.CartResponseDto;
import com.example.PayAll_BE.dto.ProductDto;
import com.example.PayAll_BE.entity.Cart;

public class CartMapper {
	public static CartResponseDto toDto(Cart cart, ProductDto productDto) {
		return CartResponseDto.builder()
			.cartId(cart.getCartId())
			.productId(cart.getProductId())
			.productName(cart.getProductName())
			.productImage(cart.getImage())
			.price(cart.getProductPrice())
			.quantity(cart.getQuantity())
			.store(productDto.getShopName())
			.link(cart.getLink()).build();
	}
}
