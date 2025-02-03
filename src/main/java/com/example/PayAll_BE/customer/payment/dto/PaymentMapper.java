package com.example.PayAll_BE.customer.payment.dto;

import com.example.PayAll_BE.customer.payment.Payment;
import com.example.PayAll_BE.customer.paymentDetails.dto.PaymentDetailDto;
import com.example.PayAll_BE.customer.paymentDetails.dto.PaymentListRequestDto;
import com.example.PayAll_BE.customer.paymentDetails.PaymentDetail;

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
			.paymentDate(paymentDate)
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

	public static PaymentResponseDto toPaymentDto(Payment payment, List<PaymentDetailDto> details) {
		return PaymentResponseDto.builder()
			.paymentPlace(payment.getPaymentPlace())
			.category(payment.getCategory().name())
			.paymentType(payment.getPaymentType().name())
			.paymentTime(payment.getPaymentTime())
			.bankName(payment.getAccount().getBankName())
			.accountName(payment.getAccount().getAccountName())
			.paymentPrice(payment.getPrice())
			.paymentDetailList(details)
			.build();
	}

	public static PaymentDetail toPaymentDetailEntity(Payment payment, PaymentListRequestDto.PaymentDetailInfoRequestDto.PurchaseProductRequestDto requestDto, Long productId) {
		return PaymentDetail.builder()
			.payment(payment)
			.productName(requestDto.getProductName())
			.productId(productId)
			.productPrice(requestDto.getPrice())
			.quantity(requestDto.getQuantity())
			.build();
	}
}
