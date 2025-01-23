package com.example.PayAll_BE.global.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.global.api.ApiResult;
import com.example.PayAll_BE.global.auth.dto.AuthRequestDto;
import com.example.PayAll_BE.global.auth.dto.AuthResponseDto;
import com.example.PayAll_BE.global.auth.dto.PlatformRequestDto;
import com.example.PayAll_BE.global.auth.dto.PlatformResponseDto;
import com.example.PayAll_BE.global.auth.dto.RegisterRequestDto;
import com.example.PayAll_BE.global.auth.service.AuthService;
import com.example.PayAll_BE.global.auth.service.JwtService;
import com.example.PayAll_BE.global.exception.BadRequestException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthService authService;
	private final JwtService jwtService;

	@Operation(
		summary = "사용자 로그인",
		description = "사용자에게 회원가입 서비스를 제공합니다."
	)
	@PostMapping("/sign-in")
	public ResponseEntity<?> login(@RequestBody AuthRequestDto request, HttpServletResponse response) throws Exception {
		AuthResponseDto authResponse = authService.login(request);

		authService.setRefreshTokenCookie(authResponse.getRefreshToken(), response);
		authService.setAccessTokenCookie(authResponse.getAccessToken(), response);

		if (authResponse.isPermission()) {
			// permission = true
			return ResponseEntity.ok(new ApiResult(200, "Already Exists", "로그인에 성공했습니다."));
		} else {
			// permission = false
			return ResponseEntity.ok(new ApiResult(200, "OK", "mydata 연동이 되어있지 않습니다."));
		}

	}

	@Operation(
		summary = "사용자 회원가입",
		description = "사용자에게 회원가입 서비스를 제공합니다."
	)
	@PostMapping("/sign-up")
	public ResponseEntity<ApiResult> register(@RequestBody RegisterRequestDto request) {
		if (request.getPassword() == null || request.getPassword().isEmpty()) {
			throw new BadRequestException("올바른 비밀번호를 입력해주세요.");
		}
		authService.register(request);
		return ResponseEntity.ok(new ApiResult(200, "OK", "회원가입이 완료되었습니다."));
	}

	@Operation(
		summary = "토큰 갱신",
		description = "리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다. 갱신된 토큰은 쿠키에 저장됩니다."
	)
	@PostMapping("/refresh")
	public ResponseEntity<ApiResult> refreshToken(HttpServletRequest request,
		HttpServletResponse response) {
		// 1. 쿠키에서 리프레시 토큰을 찾기
		String refreshToken = authService.getCookieValue(request, "refreshToken");
		AuthResponseDto newTokens = authService.refreshToken(refreshToken);

		authService.setRefreshTokenCookie(newTokens.getRefreshToken(), response);
		authService.setAccessTokenCookie(newTokens.getAccessToken(), response);

		return ResponseEntity.ok(new ApiResult(200, "OK", "토큰 갱신 성공"));
	}

	@Operation(
		summary = "플랫폼별 계정 등록",
		description = "사용자가 쿠팡, 11번가 등 플랫폼 계정을 등록합니다."
	)
	@PostMapping("/platform")
	public ResponseEntity<ApiResult> setPlatform(HttpServletRequest httpServletRequest,
		@RequestBody PlatformRequestDto request) throws
		Exception {
		String accessToken = authService.getCookieValue(httpServletRequest, "accessToken");
		String authId = jwtService.extractAuthId(accessToken);

		authService.updatePlatformInfo(authId, request);
		return ResponseEntity.ok(new ApiResult(200, "OK", "플랫폼 계정 등록 성공"));
	}

	@Operation(
		summary = "플랫폼별 계정 조회",
		description = "사용자가 쿠팡, 11번가 등 플랫폼 계정을 조회합니다."
	)
	@GetMapping("/platform")
	public ResponseEntity<ApiResult> getPlatform(HttpServletRequest httpServletRequest) throws Exception {
		String accessToken = authService.getCookieValue(httpServletRequest, "accessToken");
		String authId = jwtService.extractAuthId(accessToken);

		PlatformResponseDto platformInfo = authService.getPlatformInfo(authId);
		return ResponseEntity.ok(new ApiResult(200, "OK", "플랫폼 계정 조회 성공", platformInfo));

	}
}
