package com.example.PayAll_BE.mapper;

import com.example.PayAll_BE.dto.Card.CardResponseDto;
import com.example.PayAll_BE.entity.Card;

public class CardMapper {

	public static CardResponseDto toDto(Card card) {
		return CardResponseDto.builder()
			.cardName(card.getCardName())
			.cardType(card.getCardType().name())
			.cardCompany(card.getCardCompany())
			/*
			연회비
			전월실적
			카테고리별 혜택률 ...
			 */
			.build();
	}
}
