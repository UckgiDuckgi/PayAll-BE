package com.example.PayAll_BE.customer.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.global.api.ApiResult;
import com.example.PayAll_BE.customer.user.dto.UserResponseDto;
import com.example.PayAll_BE.global.auth.service.AuthService;
import com.example.PayAll_BE.global.exception.UnauthorizedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Tag(name = "User", description = "사용자 정보 API")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final AuthService authService;

	@Operation(
		summary = "사용자 정보 조회",
		description = "사용자의 정보를 조회합니다."
	)
	@GetMapping
	public ResponseEntity<ApiResult> getUserInfo(HttpServletRequest request) {
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		UserResponseDto userInfo = userService.getUserInfo(accessToken);

		return ResponseEntity.ok(new ApiResult(200, "OK", "사용자 정보 조회 성공", userInfo));
	}
}
