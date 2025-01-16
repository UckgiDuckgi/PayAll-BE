package com.example.PayAll_BE.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.SearchProductDto;
import com.example.PayAll_BE.product.ProductApiClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {
	private final ProductApiClient productApiClient;

	public List<SearchProductDto> getSearchProducts(String query, int page, int size) {
		return productApiClient.searchProducts(query, page, size);
	}
}
