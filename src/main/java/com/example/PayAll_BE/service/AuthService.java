package com.example.PayAll_BE.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.PayAll_BE.config.security.CryptoUtil;
import com.example.PayAll_BE.dto.AuthRequestDto;
import com.example.PayAll_BE.dto.AuthResponseDto;
import com.example.PayAll_BE.dto.PlatformRequestDto;
import com.example.PayAll_BE.dto.PlatformResponseDto;
import com.example.PayAll_BE.dto.RegisterRequestDto;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.exception.BadRequestException;
import com.example.PayAll_BE.exception.ForbiddenException;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.exception.UnauthorizedException;
import com.example.PayAll_BE.mydata.service.MydataService;
import com.example.PayAll_BE.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
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
	private final MydataService mydataService;

	public AuthResponseDto login(AuthRequestDto request) throws Exception {
		User user = userRepository.findByAuthId(request.getAuthId())
			.orElseThrow(() -> new NotFoundException("로그인 : Id에 맞는 유저를 찾을 수 없습니다."));

		if (!CryptoUtil.decrypt(user.getPassword()).equals(request.getPassword())) {
			throw new UnauthorizedException("로그인 : 잘못된 비밀번호 입니다.");
		}

		// permission = false
		if (!user.isPermission()) {
			user.setPermission(true);
			userRepository.save(user);
			return AuthResponseDto.builder()
				.accessToken(jwtService.generateAccessToken(user.getAuthId(), user.getId()))
				.refreshToken(jwtService.generateRefreshToken(user.getAuthId(), user.getId()))
				.permission(false) // 처음 로그인임을 표시
				.build();
		}

		// permission = true
		return AuthResponseDto.builder()
			.accessToken(jwtService.generateAccessToken(user.getAuthId(), user.getId()))
			.refreshToken(jwtService.generateRefreshToken(user.getAuthId(), user.getId()))
			.permission(true) // 처음 로그인이 아님
			.build();
	}

	public AuthResponseDto generateTokens(String authId, String name, Long userId) {
		String accessToken = jwtService.generateAccessToken(authId, userId);
		String refreshToken = jwtService.generateRefreshToken(authId, userId);

		// Redis에는 Refresh Token만 저장
		redisService.saveRefreshToken(authId, refreshToken, refreshTokenExpiration);

		return AuthResponseDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	public void register(RegisterRequestDto request) {
		try {
			// Validate simple password
			if (request.getPassword() == null || request.getPassword().length() < 6 || request.getPassword().length() > 12) {
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
				.password(CryptoUtil.encrypt(request.getPassword())) // 암호화 후 저장
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
		refreshTokenCookie.setMaxAge(60 * 60); //1시간 유지

		response.addCookie(refreshTokenCookie);
	}

	public void setAccessTokenCookie(String accessToken, HttpServletResponse response) {
		Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
		accessTokenCookie.setHttpOnly(true);
		accessTokenCookie.setSecure(true);
		accessTokenCookie.setPath("/");
		accessTokenCookie.setMaxAge(60 * 60); //1시간 유지

		response.addCookie(accessTokenCookie);
	}

	public PlatformResponseDto getPlatformInfo(String authId) throws Exception {
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new NotFoundException("User not found"));

		List<PlatformResponseDto.PlatformInfo> platformInfos = new ArrayList<>();

		if (user.getCoupangId() != null) {
			platformInfos.add(PlatformResponseDto.PlatformInfo.builder()
				.platformName("COUPANG")
				.id(CryptoUtil.decrypt(user.getCoupangId()))
				.build());
		}

		if (user.getElevenstId() != null) {
			platformInfos.add(PlatformResponseDto.PlatformInfo.builder()
				.platformName("11ST")
				.id(CryptoUtil.decrypt(user.getElevenstId()))
				.build());
		}

		if (user.getNaverId() != null) {
			platformInfos.add(PlatformResponseDto.PlatformInfo.builder()
				.platformName("NAVER")
				.id(CryptoUtil.decrypt(user.getNaverId()))
				.build());
		}

		return PlatformResponseDto.builder()
			.platformInfos(platformInfos).build();
	}

	public void updatePlatformInfo(String authId, PlatformRequestDto request) throws Exception {

		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new NotFoundException("User not found"));

		// 플랫폼 타입 검증 - 대소문자 구별 x
		String platformType = request.getPlatformName().toUpperCase();
		if (!isValidPlatform(platformType)) {
			throw new BadRequestException("유효하지 않은 플랫폼입니다: " + platformType);
		}

		switch (platformType) {
			case "COUPANG" -> {
				user.setCoupangId(CryptoUtil.encrypt(request.getId()));
				user.setCoupangPassword(CryptoUtil.encrypt(request.getPassword()));
			}
			case "11ST" -> {
				user.setElevenstId(CryptoUtil.encrypt(request.getId()));
				user.setElevenstPassword(CryptoUtil.encrypt(request.getPassword()));
			}
			case "NAVER" -> {
				user.setNaverId(CryptoUtil.encrypt(request.getId()));
				user.setNaverPassword(CryptoUtil.encrypt(request.getPassword()));
			}

			default -> throw new BadRequestException("유효하지 않은 플랫폼입니다: " + platformType);
		}

		userRepository.save(user);
	}

	private boolean isValidPlatform(String platformType) {
		return Arrays.asList("COUPANG", "11ST", "NAVER").contains(platformType);
	}

	// 쿠키에서 특정 이름의 값을 찾는 메서드
	public String getCookieValue(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}
