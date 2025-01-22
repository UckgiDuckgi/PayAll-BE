package com.example.PayAll_BE.customer.limit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.customer.limit.dto.LimitRegisterRequestDto;
import com.example.PayAll_BE.customer.limit.dto.LimitResponseDto;
import com.example.PayAll_BE.global.api.ApiResult;
import com.example.PayAll_BE.global.auth.service.AuthService;
import com.example.PayAll_BE.global.auth.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "Limits", description = "소비 목표 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/limit")
public class LimitController {
	private final LimitService limitService;
	private final AuthService authService;
	private final JwtService jwtService;


	@Operation(
		summary = "소비 목표 등록",
		description = "사용자가 소비 목표를 등록합니다."
	)
	@PostMapping
	public ResponseEntity<ApiResult> registerLimit(
		HttpServletRequest request,
		@RequestBody LimitRegisterRequestDto requestDto
	) {
		String token = authService.getCookieValue(request, "accessToken");
		Long userId = jwtService.extractUserId(token);

		try {
			limitService.registerLimit(userId, requestDto);
			return ResponseEntity.ok(new ApiResult(200, "OK", "소비 목표 등록 성공"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(new ApiResult(400, "Bad Request", e.getMessage()));
		}
	}

	@Operation(
		summary = "소비 목표 조회",
		description = "사용자가 소비 목표를 조회합니다."
	)
	@GetMapping
	public ResponseEntity<ApiResult> getLimit(HttpServletRequest request) {
		String token = authService.getCookieValue(request, "accessToken");
		Long userId = jwtService.extractUserId(token);
		LimitResponseDto responseDto = limitService.getLimit(userId);
		return ResponseEntity.ok(
			new ApiResult(200, "OK", "소비 목표 조회 성공", responseDto)
		);
	}
}
