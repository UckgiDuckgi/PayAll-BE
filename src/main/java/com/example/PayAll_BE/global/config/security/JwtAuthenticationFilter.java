package com.example.PayAll_BE.global.config.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.PayAll_BE.global.api.ApiResult;
import com.example.PayAll_BE.global.auth.service.AuthService;
import com.example.PayAll_BE.global.auth.service.JwtService;
import com.example.PayAll_BE.global.auth.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final RedisService redisService;
	private final AuthService authService;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();

		// Swagger 관련 경로와 OpenAPI 경로를 제외
		return path.startsWith("/swagger-ui/") ||
			path.startsWith("/v3/api-docs") ||
			path.startsWith("/swagger-resources") ||
			path.startsWith("/webjars/") ||
			path.equals("/");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		// 쿠키에서 accessToken을 추출
		String accessToken = authService.getCookieValue(request, "accessToken");

		// 토큰이 없으면 필터 체인으로 넘어감
		if (accessToken == null) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			// 토큰이 블랙리스트에 있는지 확인
			if (redisService.isBlacklisted(accessToken)) {
				ApiResult apiResult = new ApiResult(403, "FORBIDDEN", "Token is blacklisted", null);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType("application/json");
				response.getWriter().write(new ObjectMapper().writeValueAsString(apiResult));
				return;
			}

			// JWT 유효성 검사
			final String userId = jwtService.extractAuthId(accessToken);

			if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				if (jwtService.isValidToken(accessToken)) {
					List<SimpleGrantedAuthority> authorities =
						Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userId,
						null,
						authorities
					);

					authToken.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(request)
					);

					SecurityContextHolder.getContext().setAuthentication(authToken);
				} else {
					ApiResult apiResult = new ApiResult(400, "BAD_REQUEST", "Invalid token", null);
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json");
					response.getWriter().write(new ObjectMapper().writeValueAsString(apiResult));
					return;
				}
			}
		} catch (ExpiredJwtException e) {
			ApiResult apiResult = new ApiResult(401, "UNAUTHORIZED", "Token is expired", null);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write(new ObjectMapper().writeValueAsString(apiResult));
			return;
		} catch (Exception e) {
			ApiResult apiResult = new ApiResult(500, "INTERNAL_SERVER_ERROR", "Token validation failed", null);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write(new ObjectMapper().writeValueAsString(apiResult));
			return;
		}

		filterChain.doFilter(request, response);
	}
}
