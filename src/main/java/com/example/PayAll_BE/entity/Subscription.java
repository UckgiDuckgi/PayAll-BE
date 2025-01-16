package com.example.PayAll_BE.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Subscription")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Subscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subscription_id")
	private Long subscriptionId;

	@Column(name = "subscription_name", nullable = false)
	private String subscriptionName;

	@Column(name = "monthly_fee", nullable = false)
	private Long monthlyFee;

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
	public Subscription(
		String subscriptionName,
		Long monthlyFee,
		Double shoppingBenefitRate,
		Double educationBenefitRate,
		Double livingBenefitRate,
		Double transportBenefitRate,
		Double cultureBenefitRate,
		Double restaurantBenefitRate,
		Double cafeBenefitRate,
		Double healthBenefitRate
	) {
		this.subscriptionName = subscriptionName;
		this.monthlyFee = monthlyFee;
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
