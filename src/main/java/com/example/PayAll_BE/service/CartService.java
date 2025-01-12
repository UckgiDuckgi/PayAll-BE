package com.example.PayAll_BE.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.PayAll_BE.dto.CartRequestDto;
import com.example.PayAll_BE.dto.CartResponseDto;
import com.example.PayAll_BE.dto.ProductDto;
import com.example.PayAll_BE.entity.Cart;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.mapper.CartMapper;
import com.example.PayAll_BE.repository.CartRepository;
import com.example.PayAll_BE.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final RestTemplate restTemplate;

	private final String productApiUrl = "http://localhost:8081/redis/product/";
	private final UserRepository userRepository;

	public CartResponseDto addCart(CartRequestDto cartRequestDto) {

		User user = userRepository.findById(cartRequestDto.getUserId())
			.orElseThrow(() -> new EntityNotFoundException("user not found"));

		// 상품 정보 조회
		ResponseEntity<ProductDto> response = restTemplate.getForEntity(
			productApiUrl + cartRequestDto.getProductId().toString(),
			ProductDto.class);
		if (response.getBody() == null) {
			throw new RuntimeException("product not found");
		}
		ProductDto productDto = response.getBody();
		System.out.println("productDto = " + productDto);

		// 장바구니에 같은 상품 있으면 수량 +1
		Cart existingCart = cartRepository.findByProductId(cartRequestDto.getProductId());
		if (existingCart != null) {
			existingCart.setQuantity(existingCart.getQuantity() + 1);
			return CartMapper.toDto(cartRepository.save(existingCart), productDto);
		}

		Cart cart = Cart.builder()
			.user(user)
			.productId(cartRequestDto.getProductId())
			.productName(productDto.getProductName())
			.productPrice(productDto.getPrice())
			.quantity(1)
			.link(productDto.getShopUrl())
			.image(productDto.getShopImage())
			.build();

		return CartMapper.toDto(cartRepository.save(cart), productDto);

	}
}
