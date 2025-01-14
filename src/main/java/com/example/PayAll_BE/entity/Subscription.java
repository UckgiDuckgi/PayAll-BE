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
@Builder
public class Subscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subscription_id")
	private Long subscriptionId;

	@Column(name = "subscription_name", nullable = false)
	private String subscriptionName;

	@Column(name = "monthly_fee", nullable = false)
	private Long monthlyFee;

	//Todo 헤택률

	@Builder
	public Subscription(String subscriptionName, Long monthlyFee) {
		this.subscriptionName = subscriptionName;
		this.monthlyFee = monthlyFee;
	}
}
