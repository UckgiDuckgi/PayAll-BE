package com.example.PayAll_BE.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class CryptoConfig {

	@Value("${Crypto.SecretKey}")
	private String secretKey;

	@PostConstruct
	public void init() {
		CryptoUtil.setSecretKey(secretKey);  // static 필드에 값 설정
	}
}
