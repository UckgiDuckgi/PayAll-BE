package com.example.PayAll_BE.customer.product;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.customer.product.dto.ProductDescriptionDto;
import com.example.PayAll_BE.customer.product.dto.ProductMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
	public List<ProductDescriptionDto> getAllCards() {
		List<Product> cards = productRepository.findAllCards();
		return cards.stream()
			.map(ProductMapper::toDto)
			.collect(Collectors.toList());
	}

	public List<ProductDescriptionDto> getAllSubscriptions() {
		List<Product> subscribes = productRepository.findAllSubscribes();
		return subscribes.stream()
			.map(ProductMapper::toDto)
			.collect(Collectors.toList());
	}
}
