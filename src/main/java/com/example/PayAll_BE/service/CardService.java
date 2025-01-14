package com.example.PayAll_BE.service;

import com.example.PayAll_BE.dto.Card.CardResponseDto;
import com.example.PayAll_BE.entity.Card;
import com.example.PayAll_BE.mapper.CardMapper;
import com.example.PayAll_BE.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

	private final CardRepository cardRepository;

	public List<CardResponseDto> getAllCards() {
		List<Card> cards = cardRepository.findAll();
		return cards.stream()
			.map(CardMapper::toDto)
			.collect(Collectors.toList());
	}
}
