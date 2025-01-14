package com.example.PayAll_BE.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	// 기존 메소드들 유지
	public void saveValue(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	public String getValue(String key) {
		return (String)redisTemplate.opsForValue().get(key);
	}

	public boolean deleteValue(String key) {
		if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
			redisTemplate.delete(key);
			return true;
		}
		return false;
	}

	// Refresh Token 관리
	public void saveRefreshToken(String authId, String token, Long expiration) {
		redisTemplate.opsForValue().set(
			"refresh:" + authId,
			token,
			expiration,
			TimeUnit.MILLISECONDS
		);
	}

	public String getRefreshToken(String email) {
		return (String) redisTemplate.opsForValue().get("refresh:" + email);
	}

	// Blacklist 관리
	public void addToBlacklist(String token, Long expiration) {
		redisTemplate.opsForValue().set(
			"blacklist:" + token,
			"true",
			expiration,
			TimeUnit.MILLISECONDS
		);
	}

	public boolean isBlacklisted(String token) {
		return Boolean.TRUE.equals(
			redisTemplate.hasKey("blacklist:" + token)
		);
	}

	public void deleteRefreshToken(String email) {
		redisTemplate.delete("refresh:" + email);
	}
}
