package com.example.PayAll_BE.mapper;

import com.example.PayAll_BE.dto.Card.CardResponseDto;
import com.example.PayAll_BE.entity.Card;

public class CardMapper {

	public static CardResponseDto toDto(Card card) {
		return CardResponseDto.builder()
			.cardId(card.getCardId())
			.cardName(card.getCardName())
			.cardType(card.getCardType().name())
			.cardCompany(card.getCardCompany())
			.annualFee(card.getAnnualFee())
			.build();
	}
}
