package com.example.PayAll_BE.dto.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.PayAll_BE.dto.Payment.PaymentDetailResponseDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayPaymentResponseDto {
	private LocalDateTime paymentDate;
	private Long dayPaymentPrice;
	private List<PaymentDetailResponseDto> paymentDetail;
}
