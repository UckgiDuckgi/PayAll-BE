package com.example.PayAll_BE.entity;

import com.example.PayAll_BE.entity.enums.CardType;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Card")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Card {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "card_id")
	private Long cardId;

	@Column(name = "card_name", nullable = false)
	private String cardName;

	@Enumerated(EnumType.STRING)
	@Column(name = "card_type", nullable = false)
	private CardType cardType;

	@Column(name = "card_company", nullable = false)
	private String cardCompany;

	@Column(name = "annual_fee", nullable = false)
	private Long annualFee;

	@Builder
	public Card(String cardName, CardType cardType, String cardCompany, Long annualFee) {
		this.cardName = cardName;
		this.cardType = cardType;
		this.cardCompany = cardCompany;
		this.annualFee = annualFee;
	}
}
