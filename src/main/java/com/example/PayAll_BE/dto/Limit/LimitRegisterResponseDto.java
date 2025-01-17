package com.example.PayAll_BE.dto.Limit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LimitRegisterResponseDto {
	private Long limitId;
	private Long userId;
	private long limitPrice;
	private LocalDateTime limitDate; // 소비 목표 설정 날짜
}
