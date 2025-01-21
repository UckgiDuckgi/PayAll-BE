package com.example.PayAll_BE.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.ProductDescriptionDto;
import com.example.PayAll_BE.entity.Product;
import com.example.PayAll_BE.entity.Subscription;
import com.example.PayAll_BE.mapper.ProductMapper;
import com.example.PayAll_BE.repository.ProductRepository;

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
