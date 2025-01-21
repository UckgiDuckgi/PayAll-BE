package com.example.PayAll_BE.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
	private String accessToken;
	private String refreshToken;
	private boolean permission; // permission 여부 반환
}
