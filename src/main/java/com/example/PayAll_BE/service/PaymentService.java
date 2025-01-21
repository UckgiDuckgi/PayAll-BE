package com.example.PayAll_BE.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.PayAll_BE.dto.Payment.DayPaymentResponseDto;
import com.example.PayAll_BE.dto.Payment.PaymentDetailResponseDto;
import com.example.PayAll_BE.dto.Payment.PaymentResponseDto;
import com.example.PayAll_BE.dto.Payment.PaymentUpdateRequestDto;
import com.example.PayAll_BE.dto.Payment.TotalPaymentResponseDto;
import com.example.PayAll_BE.dto.PaymentDetail.PaymentDetailDto;
import com.example.PayAll_BE.dto.PaymentDetail.PaymentListRequestDto;
import com.example.PayAll_BE.dto.ProductDto;
import com.example.PayAll_BE.entity.Account;
import com.example.PayAll_BE.entity.Payment;
import com.example.PayAll_BE.entity.PaymentDetail;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.entity.enums.Category;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.mapper.PaymentDetailMapper;
import com.example.PayAll_BE.mapper.PaymentMapper;
import com.example.PayAll_BE.product.ProductApiClient;
import com.example.PayAll_BE.repository.AccountRepository;
import com.example.PayAll_BE.repository.PaymentDetailRepository;
import com.example.PayAll_BE.repository.PaymentRepository;
import com.example.PayAll_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final PaymentDetailRepository paymentDetailRepository;
	private final ProductApiClient productApiClient;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final AccountRepository accountRepository;

	public TotalPaymentResponseDto getPayments(String token, Long accountId, Category category, Pageable pageable) {
		String authId = jwtService.extractAuthId(token);
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));

		Page<Payment> paymentPage;

		if (accountId == null) {
			paymentPage = paymentRepository.findAllByUserIdAndCategory(user.getId(), category, pageable);
		} else {
			Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new NotFoundException("해당 계좌를 찾을 수 없습니다."));
			paymentPage = paymentRepository.findAllByAccountIdAndCategory(accountId, category, pageable);
		}

		if (paymentPage.isEmpty()) {
			return TotalPaymentResponseDto.builder()
				.userName(user.getName())
				.totalBalance(0L)
				.monthPaymentPrice(0L)
				.paymentList(List.of())
				.bankName(accountId != null ? accountRepository.findById(accountId).get().getBankName() : null)
				.accountName(accountId != null ? accountRepository.findById(accountId).get().getAccountName() : null)
				.accountNumber(accountId != null ? accountRepository.findById(accountId).get().getAccountNumber() : null)
				.paymentCount(0)
				.category(category)
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
						.build())
					.collect(Collectors.toList()))
				.build())
			.collect(Collectors.toList());

		Integer paymentCount = dayPaymentList.stream()
			.mapToInt(dayPayment -> dayPayment.getPaymentDetail().size())
			.sum();

		return TotalPaymentResponseDto.builder()
			.totalBalance(totalBalance)
			.monthPaymentPrice(totalPaymentPrice)
			.paymentList(dayPaymentList)
			.bankName(accountId != null ? accountRepository.findById(accountId).get().getBankName() : null)
			.accountName(accountId != null ? accountRepository.findById(accountId).get().getAccountName() : null)
			.accountNumber(accountId != null ? accountRepository.findById(accountId).get().getAccountNumber() : null)
			.paymentCount(paymentCount)
			.category(category)
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

	public void uploadPaymentDetails(String token, PaymentListRequestDto requestDto) {
		String authId = jwtService.extractAuthId(token);
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));

		for (PaymentListRequestDto.PaymentDetailInfoRequestDto paymentDetail : requestDto.getPaymentList()) {
			Payment payment = paymentRepository.findByAccount_User_IdAndPaymentTimeAndPaymentPlace(
				user.getId(), paymentDetail.getPaymentTime(), paymentDetail.getPaymentPlace()
			);

			if (payment == null) {
				throw new NotFoundException("결제 정보를 찾을 수 없습니다.");
			}

			List<PaymentDetail> paymentDetails = paymentDetail.getPurchaseProductList().stream()
				.map(product -> {
					ProductDto productDto = productApiClient.fetchProductByName(product.getProductName());
					Long productId = productDto.getPCode();
					return PaymentMapper.toPaymentDetailEntity(payment, product, productId);
				})
				.collect(Collectors.toList());

			paymentDetailRepository.saveAll(paymentDetails);
		}
	}

	@Transactional
	public void updatePaymentPlaces(List<PaymentUpdateRequestDto> paymentList) {
		List<Payment> paymentsToUpdate = new ArrayList<>();

		for (PaymentUpdateRequestDto request : paymentList) {
			Payment payment = paymentRepository.findPaymentToUpdateByAccountIdAndPaymentTime(
				request.getAccountId(), request.getPaymentTime()
			);

			if (payment != null) {
				payment.setPaymentPlace(request.getPaymentPlace());
				paymentsToUpdate.add(payment);
			} else {
				throw new NotFoundException("해당 결제 내역이 없습니다. Account ID: " + request.getAccountId());
			}
		}

		if (!paymentsToUpdate.isEmpty()) {
			paymentRepository.saveAll(paymentsToUpdate);
		}
	}
}
