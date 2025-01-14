package com.example.PayAll_BE.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.Payment.DayPaymentResponseDto;
import com.example.PayAll_BE.dto.Payment.PaymentDetailDto;
import com.example.PayAll_BE.dto.Payment.PaymentDetailResponseDto;
import com.example.PayAll_BE.dto.Payment.PaymentResponseDto;
import com.example.PayAll_BE.dto.Payment.TotalPaymentResponseDto;
import com.example.PayAll_BE.dto.ProductDto;
import com.example.PayAll_BE.entity.Payment;
import com.example.PayAll_BE.entity.PaymentDetail;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.mapper.PaymentDetailMapper;
import com.example.PayAll_BE.mapper.PaymentMapper;
import com.example.PayAll_BE.product.ProductApiClient;
import com.example.PayAll_BE.repository.PaymentDetailRepository;
import com.example.PayAll_BE.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final PaymentDetailRepository paymentDetailRepository;
	private final ProductApiClient productApiClient;

	public TotalPaymentResponseDto getPayments(Long userId) {
		List<Payment> payments = paymentRepository.findAllByUserId(userId);

		if (payments.isEmpty()) {
			return TotalPaymentResponseDto.builder()
				.totalBalance(0L)
				.monthPaymentPrice(0L)
				.paymentList(List.of())
				.build();
		}

		LocalDateTime startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
		LocalDateTime endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);


		Long totalBalance = payments.stream()
			.map(payment -> payment.getAccount().getBalance())
			.distinct()
			.mapToLong(Long::longValue)
			.sum();

		Long totalPaymentPrice = payments.stream()
			.filter(payment -> payment.getPaymentTime().isAfter(startOfMonth) && payment.getPaymentTime().isBefore(endOfMonth))
			.mapToLong(Payment::getPrice)
			.sum();

		Map<LocalDateTime, List<Payment>> groupedPayments = payments.stream()
			.collect(Collectors.groupingBy(payment -> payment.getPaymentTime().toLocalDate().atStartOfDay()));

		List<DayPaymentResponseDto> dayPaymentList = groupedPayments.entrySet().stream()
			.map(entry -> DayPaymentResponseDto.builder()
				.paymentDate(entry.getKey().toLocalDate().atStartOfDay())
				.dayPaymentPrice(entry.getValue().stream().mapToLong(Payment::getPrice).sum())
				.paymentDetail(entry.getValue().stream().map(payment -> PaymentDetailResponseDto.builder()
						.paymentPlace(payment.getPaymentPlace())
						.category(payment.getCategory().name())
						.paymentPrice(payment.getPrice())
						.paymentType(payment.getPaymentType().name())
						.paymentTime(payment.getPaymentTime())
						.bankName(payment.getAccount().getBankName())
						.accountName(payment.getAccount().getAccountName())
						.shootNeed(false)
						.build())
					.collect(Collectors.toList()))
				.build())
			.collect(Collectors.toList());

		return TotalPaymentResponseDto.builder()
			.totalBalance(totalBalance)
			.monthPaymentPrice(totalPaymentPrice)
			.paymentList(dayPaymentList)
			.build();
	}

	public PaymentResponseDto getPaymentById(Long paymentId) {
		Payment payment = paymentRepository.findById(paymentId)
			.orElseThrow(() -> new NotFoundException("결제 내역을 찾을 수 없습니다."));

		List<PaymentDetail> paymentDetails = paymentDetailRepository.findByPaymentId(paymentId);
		List<PaymentDetailDto> paymentDetailDtos = paymentDetails.stream().map(paymentDetail -> {
			ProductDto productDto = productApiClient.fetchProduct(String.valueOf(paymentDetail.getProductId()));
			return PaymentDetailMapper.toDto(paymentDetail, productDto);
		}).collect(Collectors.toList());

		return PaymentMapper.toPaymentDto(payment, paymentDetailDtos);
	}
}
