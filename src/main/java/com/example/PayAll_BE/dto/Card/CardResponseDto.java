package com.example.PayAll_BE.dto.Card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CardResponseDto {
	private String cardName;
	private String cardType;
	private String cardCompany;
	// 연회비, 전월실적, 카테고리별 혜택률...
}
