package com.example.PayAll_BE.mapper;

import com.example.PayAll_BE.dto.Payment.DayPaymentResponseDto;
import com.example.PayAll_BE.dto.Payment.PaymentDetailResponseDto;
import com.example.PayAll_BE.dto.Payment.TotalPaymentResponseDto;
import com.example.PayAll_BE.entity.Payment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentMapper {

	public static PaymentDetailResponseDto toPaymentDetailResponseDto(Payment payment) {
		return PaymentDetailResponseDto.builder()
			.paymentPlace(payment.getPaymentPlace())
			.category(payment.getCategory().name())
			.paymentPrice(payment.getPrice())
			.paymentType(payment.getPaymentType().name())
			.paymentTime(payment.getPaymentTime())
			.bankName(payment.getAccount().getBankName())
			.accountName(payment.getAccount().getAccountName())
			.shootNeed(true)
			.build();
	}

	public static DayPaymentResponseDto toDayPaymentResponseDto(List<Payment> payments) {
		LocalDateTime paymentDate = payments.get(0).getPaymentTime();  // LocalDateTime 사용
		List<PaymentDetailResponseDto> paymentDetails = payments.stream()
			.map(PaymentMapper::toPaymentDetailResponseDto)
			.collect(Collectors.toList());

		Long dayPaymentPrice = payments.stream()
			.mapToLong(Payment::getPrice)
			.sum();

		return DayPaymentResponseDto.builder()
			.paymentDate(paymentDate)  // LocalDateTime 설정
			.dayPaymentPrice(dayPaymentPrice)
			.paymentDetail(paymentDetails)
			.build();
	}


	public static TotalPaymentResponseDto toTotalPaymentResponseDto(Long totalBalance, Long monthPaymentPrice, List<DayPaymentResponseDto> paymentList) {
		return TotalPaymentResponseDto.builder()
			.totalBalance(totalBalance)
			.monthPaymentPrice(monthPaymentPrice)
			.paymentList(paymentList)
			.build();
	}
}
