package com.example.PayAll_BE.dto.Card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CardResponseDto {
	private Long cardId;
	private String cardName;
	private String cardType;
	private String cardCompany;
	private Long annualFee;
}
