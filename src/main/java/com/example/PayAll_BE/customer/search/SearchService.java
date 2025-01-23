package com.example.PayAll_BE.customer.search;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.global.crawlingProduct.CrawlingProductApiClient;
import com.example.PayAll_BE.global.crawlingProduct.CrawlingProductDto;
import com.example.PayAll_BE.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
	private final CrawlingProductApiClient crawlingProductApiClient;

	public List<SearchProductDto> getSearchProducts(String query, int page, int size) {
		return crawlingProductApiClient.searchProducts(query, page, size);
	}

	public void productInfoToRedis(Long productId) {
		try {
			CrawlingProductDto crawlingProductDto = crawlingProductApiClient.fetchProduct(String.valueOf(productId));
			log.info("상품이 redis에 존재합니다.");
		} catch (NotFoundException e) {
			// redis에 상품이 없으면 크롤링 및 redis 저장 요청
			crawlingProductApiClient.requestCrawling(String.valueOf(productId));
		}
	}

}
