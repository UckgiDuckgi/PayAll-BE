package com.example.PayAll_BE.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.SearchProductDto;
import com.example.PayAll_BE.service.SearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {
	private final SearchService searchService;

	@GetMapping
	public ResponseEntity<ApiResult> getSearchProducts(@RequestParam String query,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "5") int size) {

		List<SearchProductDto> searchProducts = searchService.getSearchProducts(query, page, size);

		if (searchProducts.isEmpty()) {
			return ResponseEntity.ok(new ApiResult(200, "OK", "검색 결과가 없습니다.", Collections.emptyList()));
		}

		return ResponseEntity.ok(new ApiResult(200, "OK", "상품 검색 성공", searchProducts));
	}
}
