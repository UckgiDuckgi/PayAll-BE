package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.AuthRequestDto;
import com.example.PayAll_BE.dto.AuthResponseDto;
import com.example.PayAll_BE.dto.PlatformRequestDto;
import com.example.PayAll_BE.dto.RefreshTokenRequestDto;
import com.example.PayAll_BE.dto.RegisterRequestDto;
import com.example.PayAll_BE.exception.BadRequestException;
import com.example.PayAll_BE.service.AuthService;
import com.example.PayAll_BE.service.JwtService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cert")
public class AuthController {
	private final AuthService authService;
	private final JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequestDto request, HttpServletResponse response) throws Exception {
		AuthResponseDto authResponse = authService.login(request);

		authService.setRefreshTokenCookie(authResponse.getRefreshToken(), response);

		return ResponseEntity.ok(new ApiResult(200, "OK", "토큰 갱신 성공", authResponse));
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResult> register(@RequestBody RegisterRequestDto request) {
		if (request.getPassword() == null || request.getPassword().isEmpty()) {
			throw new BadRequestException("올바른 비밀번호를 입력해주세요.");
		}
		authService.register(request);
		return ResponseEntity.ok(new ApiResult(200, "OK", "회원가입이 완료되었습니다."));
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResult> refreshToken(@RequestBody RefreshTokenRequestDto request,
		HttpServletResponse response) {
		AuthResponseDto newTokens = authService.refreshToken(request.getRefreshToken());

		authService.setRefreshTokenCookie(newTokens.getRefreshToken(), response);

		return ResponseEntity.ok(new ApiResult(200, "OK", "토큰 갱신 성공", newTokens));
	}

	@GetMapping("/test")
	public String test(@RequestHeader("Authorization") String token) {
		String userId = jwtService.extractAuthId(token.replace("Bearer ", ""));
		return userId;
	}

	@PostMapping("platform")
	public ResponseEntity<ApiResult> setPlatform(@RequestHeader("Authorization") String token,@RequestBody PlatformRequestDto request) throws
		Exception {
		String authId = jwtService.extractAuthId(token.replace("Bearer ", ""));

		authService.updatePlatformInfo(authId,request);

		return ResponseEntity.ok(new ApiResult(200, "OK", "플랫폼 계정 등록에 성공하였습니다."));
	}

	// @GetMapping("platform")
	// public ResponseEntity<ApiResult> getPlatform(@RequestHeader("Authorization") String token,@RequestBody PlatformRequestDto request) throws
	// 	Exception {
	// 	String authId = jwtService.extractAuthId(token.replace("Bearer ", ""));
	//
	// 	authService.updatePlatformInfo(authId,request);
	//
	// 	return ResponseEntity.ok(new ApiResult(200, "OK", "플랫폼 계정 등록에 성공하였습니다."));
	// }
}
