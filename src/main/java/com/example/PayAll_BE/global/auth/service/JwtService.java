package com.example.PayAll_BE.global.auth.service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.example.PayAll_BE.global.mydata.service.MydataService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {
	private final MydataService mydataService;

	public JwtService(@Lazy MydataService mydataService) {
		this.mydataService = mydataService;
	}

	@Value("${jwt.secret}")
	private String secret;

	@Getter
	@Value("${jwt.access.token.expiration}")
	private Long accessTokenExpiration;

	@Getter
	@Value("${jwt.refresh.token.expiration}")
	private Long refreshTokenExpiration;

	public String generateAccessToken(String authId, Long userId) {
		String token = buildToken(authId, userId, accessTokenExpiration);
		syncMyData(token);
		return token;
	}

	public String generateRefreshToken(String authId, Long userId) {
		String token = buildToken(authId, userId, refreshTokenExpiration);
		syncMyData(token);
		return token;
	}

	private String buildToken(String authId, Long userId, Long expiration) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", userId);
		claims.put("roles", Collections.singletonList("ROLE_USER"));

		return Jwts.builder()
			.setClaims(claims)
			.setSubject(authId)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expiration))
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String extractAuthId(String token) {
		return extractAllClaims(token).getSubject();
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(getSigningKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public Long extractUserId(String token) {
		return extractAllClaims(token).get("userId", Long.class);
	}

	public boolean isValidToken(String token) {
		try {
			// todo 블랙리스트 체크
			//if (redisService.isBlacklisted(token)) {
			//    return false;
			//}

			// JWT 유효성 검증
			Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token.replace("Bearer ", ""));

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// 마이데이터 연동 로직
	private void syncMyData(String token) {
		try {
			mydataService.syncMydataInfo(token);
			log.info("마이데이터 연동 성공");
		} catch (Exception e) {
			log.error("마이데이터 연동 실패 : {} ", e.getMessage());
		}
	}
}
