package com.example.PayAll_BE.customer.search;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.global.crawlingProduct.CrawlingProductApiClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {
	private final CrawlingProductApiClient crawlingProductApiClient;

	public List<SearchProductDto> getSearchProducts(String query, int page, int size) {
		return crawlingProductApiClient.searchProducts(query, page, size);
	}
}
