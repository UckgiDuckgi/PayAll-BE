package com.example.PayAll_BE.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.AuthRequestDto;
import com.example.PayAll_BE.dto.AuthResponseDto;
import com.example.PayAll_BE.dto.RegisterRequestDto;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.exception.BadRequestException;
import com.example.PayAll_BE.exception.ForbiddenException;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.exception.UnauthorizedException;
import com.example.PayAll_BE.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	@Getter
	@Value("${jwt.access.token.expiration}")
	private Long accessTokenExpiration;

	@Getter
	@Value("${jwt.refresh.token.expiration}")
	private Long refreshTokenExpiration;

	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final RedisService redisService;

	public AuthResponseDto login(AuthRequestDto request) {
		User user = userRepository.findByAuthId(request.getAuthId())
			.orElseThrow(() -> new NotFoundException("로그인 : User not found"));

		if (!user.getPassword().equals(request.getPassword())) {
			throw new UnauthorizedException("로그인 : Invalid password");
		}

		return generateTokens(user.getAuthId(), user.getName(), user.getId());
	}

	public AuthResponseDto generateTokens(String authId, String name, Long userId) {
		String accessToken = jwtService.generateAccessToken(authId, userId);
		String refreshToken = jwtService.generateRefreshToken(authId, userId);

		// Redis에는 Refresh Token만 저장
		redisService.saveRefreshToken(authId, refreshToken, refreshTokenExpiration);

		return AuthResponseDto.builder()
			.code(200)
			.status("OK")
			.message("로그인이 완료되었습니다.")
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	public void register(RegisterRequestDto request) {
		try {
			// Validate simple password
			if (request.getPassword() == null || request.getPassword().length() < 6) {
				throw new BadRequestException("올바른 비밀번호를 입력해주세요.");
			}

			// 전화번호 형식 통일
			String formattedPhone = request.getPhone().replaceAll("-", "");

			Optional<User> existingUser = userRepository.findAll().stream()
				.filter(user ->
					user.getName().equals(request.getName()) &&
						user.getPhone().replaceAll("-", "").equals(formattedPhone)
				)
				.findFirst();

			// 기존 사용자가 있는 경우
			if (existingUser.isPresent()) {
				User user = existingUser.get();
				throw new BadRequestException("이미 가입된 사용자 입니다.");
			}

			// 새로운 사용자 생성
			User newUser = User.builder()
				.authId(request.getAuthId())
				.name(request.getName())
				.phone(formattedPhone)
				.address(request.getAddress())
				.password(request.getPassword())
				.build();

			userRepository.save(newUser);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("회원가입 실패: " + e.getMessage());
		}
	}

	public AuthResponseDto refreshToken(String refreshToken) {
		if (!jwtService.isValidToken(refreshToken)) {
			throw new ForbiddenException("유효하지 않은 리프레시 토큰입니다.");
		}

		String authId = jwtService.extractAuthId(refreshToken);
		Long userId = jwtService.extractUserId(refreshToken);

		// Redis에 저장된 리프레시 토큰과 비교
		String storedRefreshToken = redisService.getRefreshToken(authId);
		if (!refreshToken.equals(storedRefreshToken)) {
			throw new UnauthorizedException("토큰이 일치하지 않습니다.");
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

		return generateTokens(authId, user.getName(), userId);
	}

	public void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setSecure(true);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(60 * 60 * 24 * 30);

		response.addCookie(refreshTokenCookie);
	}
}
