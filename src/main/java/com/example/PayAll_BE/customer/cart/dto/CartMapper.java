package com.example.PayAll_BE.customer.cart.dto;

import com.example.PayAll_BE.customer.cart.Cart;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.global.crawlingProduct.CrawlingProductDto;

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

	public static Cart toCart(User user, CartRequestDto cartRequestDto, CrawlingProductDto crawlingDto) {
		return Cart.builder()
			.user(user)
			.productId(cartRequestDto.getProductId())
			.productName(crawlingDto.getProductName())
			.productPrice(crawlingDto.getPrice())
			.quantity(cartRequestDto.getQuantity())
			.link(crawlingDto.getShopUrl())
			.image(crawlingDto.getProductImage())
			.storeName(crawlingDto.getShopName())
			.prevPrice(cartRequestDto.getPrevPrice())
			.build();
	}

	public static Cart toCart(User user, CartRequestDto cartRequestDto) {
		return Cart.builder()
			.user(user)
			.productId(cartRequestDto.getProductId())
			.productName(cartRequestDto.getProductName())
			.productPrice(cartRequestDto.getPrice())
			.quantity(cartRequestDto.getQuantity())
			.link(cartRequestDto.getShopUrl())
			.image(cartRequestDto.getProductImage())
			.storeName(cartRequestDto.getShopName())
			.prevPrice(cartRequestDto.getPrevPrice())
			.build();
	}
}
