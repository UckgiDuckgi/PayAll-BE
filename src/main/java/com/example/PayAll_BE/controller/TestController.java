package com.example.PayAll_BE.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.entity.enums.Category;
import com.example.PayAll_BE.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {
	private final CategoryService categoryService;

	@GetMapping("/test")
	public Category categoryTest() {
		return categoryService.getCategory("무신사");
	}
}
