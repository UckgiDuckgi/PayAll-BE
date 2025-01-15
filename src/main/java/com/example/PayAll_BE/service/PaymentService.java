package com.example.PayAll_BE.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.Payment.DayPaymentResponseDto;
import com.example.PayAll_BE.dto.Payment.PaymentDetailDto;
import com.example.PayAll_BE.dto.Payment.PaymentDetailResponseDto;
import com.example.PayAll_BE.dto.Payment.PaymentResponseDto;
import com.example.PayAll_BE.dto.Payment.TotalPaymentResponseDto;
import com.example.PayAll_BE.dto.ProductDto;
import com.example.PayAll_BE.entity.Payment;
import com.example.PayAll_BE.entity.PaymentDetail;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.mapper.PaymentDetailMapper;
import com.example.PayAll_BE.mapper.PaymentMapper;
import com.example.PayAll_BE.product.ProductApiClient;
import com.example.PayAll_BE.repository.PaymentDetailRepository;
import com.example.PayAll_BE.repository.PaymentRepository;
import com.example.PayAll_BE.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final PaymentDetailRepository paymentDetailRepository;
	private final ProductApiClient productApiClient;
	private final JwtService jwtService;
	private final UserRepository userRepository;

	public TotalPaymentResponseDto getPayments(HttpServletRequest request, String category, Pageable pageable) {
		String token = request.getHeader("Authorization").replace("Bearer ", "");
		String authId = jwtService.extractAuthId(token);
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));
		Page<Payment> paymentPage = paymentRepository.findAllByUserIdAndCategory(user.getId(), category, pageable);

		if (paymentPage.isEmpty()) {
			return TotalPaymentResponseDto.builder()
				.totalBalance(0L)
				.monthPaymentPrice(0L)
				.paymentList(List.of())
				.build();
		}

		List<Payment> payments = paymentPage.getContent();
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
