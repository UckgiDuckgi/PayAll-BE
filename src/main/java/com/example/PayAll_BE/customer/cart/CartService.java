package com.example.PayAll_BE.customer.cart;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.customer.cart.dto.CartMapper;
import com.example.PayAll_BE.customer.cart.dto.CartRequestDto;
import com.example.PayAll_BE.customer.cart.dto.CartResponseDto;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.crawlingProduct.CrawlingProductApiClient;
import com.example.PayAll_BE.global.crawlingProduct.CrawlingProductDto;
import com.example.PayAll_BE.global.exception.BadRequestException;
import com.example.PayAll_BE.global.exception.ForbiddenException;
import com.example.PayAll_BE.global.exception.NotFoundException;
import com.example.PayAll_BE.global.exception.UnauthorizedException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final CrawlingProductApiClient crawlingProductApiClient;
	private final UserRepository userRepository;

	public CartResponseDto addCart(String authId, CartRequestDto cartRequestDto) {

		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new UnauthorizedException("유효하지 않은 사용자입니다."));

		Cart cart;
		if (cartRequestDto.isSearch()) {
			// 장바구니에 같은 상품 있는지 확인
			Cart existingCart = cartRepository.findByUserIdAndProductIdAndProductPriceAndStoreName(user.getId(),
				cartRequestDto.getProductId(), cartRequestDto.getPrice(), cartRequestDto.getShopName()).orElse(null);
			if (existingCart != null) {
				existingCart.setQuantity(existingCart.getQuantity() + cartRequestDto.getQuantity());
				return CartMapper.toDto(cartRepository.save(existingCart));
			}

			cart = CartMapper.toCart(user, cartRequestDto);
		} else {
			CrawlingProductDto crawlingProductDto = crawlingProductApiClient.fetchProduct(
				String.valueOf(cartRequestDto.getProductId()));

			// 장바구니에 같은 상품 있는지 확인
			Cart existingCart = cartRepository.findByUserIdAndProductIdAndProductPriceAndStoreName(user.getId(),
					cartRequestDto.getProductId(), crawlingProductDto.getPrice(), crawlingProductDto.getShopName())
				.orElse(null);
			if (existingCart != null) {
				existingCart.setQuantity(existingCart.getQuantity() + cartRequestDto.getQuantity());
				return CartMapper.toDto(cartRepository.save(existingCart));
			}

			cart = CartMapper.toCart(user, cartRequestDto, crawlingProductDto);
		}

		return CartMapper.toDto(cartRepository.save(cart));

	}

	public List<CartResponseDto> getCarts(String authId) {
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new UnauthorizedException("유효하지 않은 사용자입니다."));

		List<Cart> carts = cartRepository.findAllByUserId(user.getId());

		return carts.stream().map(CartMapper::toDto).toList();

	}

	public void updateQuantity(Long cartId, int quantity, String authId) {
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new UnauthorizedException("유효하지 않은 사용자입니다."));

		if (quantity < 1) {
			throw new BadRequestException("수량은 1 이상이어야 합니다.");
		}

		Cart cart = cartRepository.findById(cartId)
			.orElseThrow(() -> new NotFoundException("해당 장바구니 항목을 찾을 수 없습니다."));

		if (!cart.getUser().getId().equals(user.getId())) {
			throw new ForbiddenException("장바구니 수량을 수정할 수 없습니다.");
		}

		cart.setQuantity(quantity);
		cartRepository.save(cart);

	}

	public void deleteCart(Long cartId, String authId) {

		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new UnauthorizedException("유효하지 않은 사용자입니다."));
		Cart cart = cartRepository.findById(cartId)
			.orElseThrow(() -> new EntityNotFoundException("해당 장바구니 항목을 찾을 수 없습니다."));

		if (!cart.getUser().getId().equals(user.getId())) {
			throw new ForbiddenException("장바구니를 삭제할 수 없습니다.");
		}

		cartRepository.delete(cart);
	}

	public void deleteCarts(List<Long> cartIds, String authId) {
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new UnauthorizedException("유효하지 않은 사용자입니다."));

		List<Cart> carts = cartRepository.findAllById(cartIds);

		carts.forEach(
			cart -> {
				if (!cart.getUser().getId().equals(user.getId())) {
					throw new ForbiddenException("장바구니를 삭제할 수 없습니다.");
				}
			}
		);

		cartRepository.deleteAllById(cartIds);
	}

}
