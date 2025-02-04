package com.example.PayAll_BE.customer.payment;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.PayAll_BE.customer.account.Account;
import com.example.PayAll_BE.customer.account.AccountRepository;
import com.example.PayAll_BE.customer.enums.Category;
import com.example.PayAll_BE.customer.payment.dto.DayPaymentResponseDto;
import com.example.PayAll_BE.customer.payment.dto.PaymentDetailResponseDto;
import com.example.PayAll_BE.customer.payment.dto.PaymentMapper;
import com.example.PayAll_BE.customer.payment.dto.PaymentResponseDto;
import com.example.PayAll_BE.customer.payment.dto.PaymentUpdateRequestDto;
import com.example.PayAll_BE.customer.payment.dto.TotalPaymentResponseDto;
import com.example.PayAll_BE.customer.paymentDetails.PaymentDetail;
import com.example.PayAll_BE.customer.paymentDetails.PaymentDetailRepository;
import com.example.PayAll_BE.customer.paymentDetails.dto.PaymentDetailDto;
import com.example.PayAll_BE.customer.paymentDetails.dto.PaymentDetailMapper;
import com.example.PayAll_BE.customer.paymentDetails.dto.PaymentListRequestDto;
import com.example.PayAll_BE.customer.purchase.PurchaseRequestDto;
import com.example.PayAll_BE.customer.user.User;
import com.example.PayAll_BE.customer.user.UserRepository;
import com.example.PayAll_BE.global.auth.service.JwtService;
import com.example.PayAll_BE.global.crawlingProduct.CrawlingProductApiClient;
import com.example.PayAll_BE.global.crawlingProduct.CrawlingProductDto;
import com.example.PayAll_BE.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final PaymentDetailRepository paymentDetailRepository;
	private final CrawlingProductApiClient crawlingProductApiClient;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final AccountRepository accountRepository;

	private static final Map<String, String> STORE = Map.of(
		"Coupang", "쿠팡",
		"11st", "11번가"
	);

	public TotalPaymentResponseDto getPayments(String token, Long accountId, Category category) {
		String authId = jwtService.extractAuthId(token);
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));

		List<Payment> payments;

		if (accountId == null) {
			// 통합 계좌: 모든 계좌의 Payment 조회
			payments = (category == null)
				? paymentRepository.findAllByUserId(user.getId())
				: paymentRepository.findAllByUserIdAndCategory(user.getId(), category);
		} else {
			// 특정 계좌의 Payment 조회
			payments = (category == null)
				? paymentRepository.findAllByAccountId(accountId)
				: paymentRepository.findAllByAccountIdAndCategory(accountId, category);
		}

		if (payments.isEmpty()) {
			return TotalPaymentResponseDto.builder()
				.userName(user.getName())
				.totalBalance(0L)
				.monthPaymentPrice(0L)
				.paymentList(List.of())
				.bankName(accountId != null ? accountRepository.findById(accountId).get().getBankName() : null)
				.accountName(accountId != null ? accountRepository.findById(accountId).get().getAccountName() : null)
				.accountNumber(
					accountId != null ? accountRepository.findById(accountId).get().getAccountNumber() : null)
				.paymentCount(0)
				.category(category)
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
			.filter(payment -> payment.getPaymentTime().isAfter(startOfMonth) && payment.getPaymentTime()
				.isBefore(endOfMonth))
			.mapToLong(Payment::getPrice)
			.sum();

		Map<LocalDateTime, List<Payment>> groupedPayments = payments.stream()
			.collect(Collectors.groupingBy(payment -> payment.getPaymentTime().toLocalDate().atStartOfDay()));

		List<DayPaymentResponseDto> dayPaymentList = groupedPayments.entrySet().stream()
			.sorted(Map.Entry.<LocalDateTime, List<Payment>>comparingByKey().reversed())
			.map(entry -> DayPaymentResponseDto.builder()
				.paymentDate(entry.getKey().toLocalDate().atStartOfDay())
				.dayPaymentPrice(entry.getValue().stream().mapToLong(Payment::getPrice).sum())
				.paymentDetail(entry.getValue().stream().map(payment -> PaymentDetailResponseDto.builder()
						.paymentId(payment.getId())
						.paymentPlace(payment.getPaymentPlace())
						.category(payment.getCategory() != null ? payment.getCategory().name() : "전체")
						.paymentPrice(payment.getPrice())
						.paymentType(payment.getPaymentType().name())
						.paymentTime(payment.getPaymentTime())
						.bankName(payment.getAccount().getBankName())
						.accountName(payment.getAccount().getAccountName())
						.shootNeed(payment.getPaymentDetails().isEmpty())
						.build())
					.collect(Collectors.toList()))
				.build())
			.collect(Collectors.toList());

		Integer paymentCount = dayPaymentList.stream()
			.mapToInt(dayPayment -> dayPayment.getPaymentDetail().size())
			.sum();

		return TotalPaymentResponseDto.builder()
			.userName(user.getName())
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
			if (paymentDetail.getProductId() != null) {
				CrawlingProductDto crawlingProductDto = crawlingProductApiClient.fetchProduct(
					String.valueOf(paymentDetail.getProductId()));
				return PaymentDetailMapper.toDto(paymentDetail, crawlingProductDto);
			} else {
				return PaymentDetailMapper.toDto(paymentDetail);
			}
		}).collect(Collectors.toList());

		return PaymentMapper.toPaymentDto(payment, paymentDetailDtos);
	}

	public void uploadPaymentDetails(String token, PaymentListRequestDto requestDto) {
		String authId = jwtService.extractAuthId(token);
		User user = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));

		for (PaymentListRequestDto.PaymentDetailInfoRequestDto paymentDetail : requestDto.getPaymentList()) {
			LocalDateTime requestPaymentTime = Instant.ofEpochMilli(paymentDetail.getPaymentTime())
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();

			Payment payment;

			if ("11번가".equals(paymentDetail.getPaymentPlace())) {
				LocalDate requestDate = requestPaymentTime.toLocalDate();
				LocalDateTime startOfDay = requestDate.atStartOfDay();
				LocalDateTime endOfDay = requestDate.atTime(LocalTime.MAX);

				Optional<Payment> optionalPayment = paymentRepository.findFirstByAccount_User_IdAndPaymentTimeBetween(
					user.getId(), startOfDay, endOfDay
				);

				payment = optionalPayment.orElse(null);
			} else {
				payment = paymentRepository.findByAccount_User_IdAndPaymentTimeAndPaymentPlace(
					user.getId(), requestPaymentTime, paymentDetail.getPaymentPlace()
				);
			}

			if (payment == null) {
				System.out.println("해당 결제 내역을 찾을 수 없습니다.");
				continue;
			}

			List<PaymentDetail> existingPaymentDetails = paymentDetailRepository.findByPayment(payment);

			List<PaymentDetail> newPaymentDetails = paymentDetail.getPurchaseProductList().stream()
				.filter(product -> existingPaymentDetails.stream()
					.noneMatch(existingDetail ->
						existingDetail.getProductName().equals(product.getProductName()) &&
							existingDetail.getProductPrice().equals(product.getPrice()) &&
							existingDetail.getQuantity() == product.getQuantity()
					)
				)
				.map(product -> {
					CrawlingProductDto crawlingProductDto = crawlingProductApiClient.fetchProductByName(
						product.getProductName());
					Long productId = crawlingProductDto.getPCode();
					return PaymentMapper.toPaymentDetailEntity(payment, product, productId);
				})
				.collect(Collectors.toList());

			if (!newPaymentDetails.isEmpty()) {
				paymentDetailRepository.saveAll(newPaymentDetails);
			}
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

	public void createPaymentDetails(Long userId, String accountNum,
		List<PurchaseRequestDto.PurchaseProductDto> products) {
		Account account = accountRepository.findByUserIdAndAccountNumber(userId, accountNum)
			.orElseThrow(() -> new NotFoundException("account not found"));

		products.forEach(product -> {
			Payment payment = paymentRepository.findFirstByAccountIdAndPaymentPlaceOrderByPaymentTimeDesc(
					account.getId(), STORE.getOrDefault(product.getStoreName(), product.getStoreName()))
				.orElseThrow(() -> new NotFoundException("payment not found"));
			PaymentDetail detail = PaymentDetail.builder()
				.payment(payment)
				.productId(product.getProductId())
				.productName(product.getProductName())
				.productPrice(product.getProductPrice())
				.quantity(product.getQuantity())
				.build();
			paymentDetailRepository.save(detail);

		});
	}
}
