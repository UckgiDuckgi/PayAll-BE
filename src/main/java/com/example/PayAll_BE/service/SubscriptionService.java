package com.example.PayAll_BE.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.Subscription.SubscriptionResponseDto;
import com.example.PayAll_BE.entity.Subscription;
import com.example.PayAll_BE.mapper.SubscriptionMapper;
import com.example.PayAll_BE.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
	private final SubscriptionRepository subscriptionRepository;

	public List<SubscriptionResponseDto> getAllSubscriptions() {
		List<Subscription> subscriptions = subscriptionRepository.findAll();
		return subscriptions.stream()
			.map(SubscriptionMapper::toDto)
			.collect(Collectors.toList());
	}
}
