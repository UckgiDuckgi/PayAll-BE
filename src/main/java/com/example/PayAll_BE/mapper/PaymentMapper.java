package com.example.PayAll_BE.mapper;

import com.example.PayAll_BE.dto.Payment.DayPaymentResponseDto;
import com.example.PayAll_BE.dto.Payment.PaymentDetailResponseDto;
import com.example.PayAll_BE.dto.Payment.TotalPaymentResponseDto;
import com.example.PayAll_BE.dto.Payment.PaymentResponseDto;
import com.example.PayAll_BE.dto.PaymentDetail.PaymentDetailDto;
import com.example.PayAll_BE.dto.PaymentDetail.PaymentDetailInfoRequestDto;
import com.example.PayAll_BE.dto.ProductDto;
import com.example.PayAll_BE.entity.Payment;
import com.example.PayAll_BE.entity.PaymentDetail;

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

	public static PaymentDetail toPaymentDetailEntity(Payment payment, PaymentDetailInfoRequestDto.PurchaseProductRequestDto requestDto, String productId) {
		return PaymentDetail.builder()
			.payment(payment)  // 연관된 Payment 엔티티 설정
			.productName(requestDto.getProductName())
			.productId(productId)
			.productPrice(requestDto.getPrice())
			.quantity(requestDto.getAmount())
			.build();
	}
}
