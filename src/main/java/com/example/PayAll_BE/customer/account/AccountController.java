package com.example.PayAll_BE.customer.account;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.global.api.ApiResult;
import com.example.PayAll_BE.global.exception.UnauthorizedException;
import com.example.PayAll_BE.global.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Account", description = "계좌 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {
	private final AccountService accountService;
	private final AuthService authService;

	@Operation(
		summary = "전체 계좌 목록 정보 조회",
		description = "사용자의 계좌 목록을 조회합니다."
	)
	@GetMapping
	public ResponseEntity<ApiResult> getAccounts(HttpServletRequest request){
		String accessToken = authService.getCookieValue(request, "accessToken");
		if(accessToken == null){
			throw new UnauthorizedException("액세스 토큰이 없습니다");
		}
		return ResponseEntity.ok(new ApiResult(200, "OK", "사용자 계좌 목록 조회 성공", accountService.getUserAccounts(accessToken)));
	}
}
