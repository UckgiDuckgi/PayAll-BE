package com.example.PayAll_BE.dto.Subscription;

import lombok.Data;
import java.util.List;

import com.example.PayAll_BE.dto.Payment.PaymentRequestDto;

@Data
public class SubscriptionRequestDto {
	private List<PaymentRequestDto> payments;  // 소비 내역
}
