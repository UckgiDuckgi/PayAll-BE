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

	//전월 실적
	@Column(name = "monthly_spending_requirement", nullable = false)
	private Long monthlySpendingRequirement;

	@Column(name = "shopping_benefit_rate", nullable = false)
	private Double shoppingBenefitRate;

	@Column(name = "education_benefit_rate", nullable = false)
	private Double educationBenefitRate;

	@Column(name = "living_benefit_rate", nullable = false)
	private Double livingBenefitRate;

	@Column(name = "transport_benefit_rate", nullable = false)
	private Double transportBenefitRate;

	@Column(name = "culture_benefit_rate", nullable = false)
	private Double cultureBenefitRate;

	@Column(name = "restaurant_benefit_rate", nullable = false)
	private Double restaurantBenefitRate;

	@Column(name = "cafe_benefit_rate", nullable = false)
	private Double cafeBenefitRate;

	@Column(name = "health_benefit_rate", nullable = false)
	private Double healthBenefitRate;

	@Builder
	public Card(
		String cardName,
		CardType cardType,
		String cardCompany,
		Long annualFee,
		Double shoppingBenefitRate,
		Double educationBenefitRate,
		Double livingBenefitRate,
		Double transportBenefitRate,
		Double cultureBenefitRate,
		Double restaurantBenefitRate,
		Double cafeBenefitRate,
		Double healthBenefitRate
	) {
		this.cardName = cardName;
		this.cardType = cardType;
		this.cardCompany = cardCompany;
		this.annualFee = annualFee;
		this.shoppingBenefitRate = shoppingBenefitRate;
		this.educationBenefitRate = educationBenefitRate;
		this.livingBenefitRate = livingBenefitRate;
		this.transportBenefitRate = transportBenefitRate;
		this.cultureBenefitRate = cultureBenefitRate;
		this.restaurantBenefitRate = restaurantBenefitRate;
		this.cafeBenefitRate = cafeBenefitRate;
		this.healthBenefitRate = healthBenefitRate;
	}
}
