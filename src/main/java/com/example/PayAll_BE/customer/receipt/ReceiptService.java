package com.example.PayAll_BE.customer.receipt;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.customer.receipt.dto.ReceiptRequestDto;
import com.example.PayAll_BE.customer.payment.Payment;
import com.example.PayAll_BE.customer.paymentDetails.PaymentDetail;
import com.example.PayAll_BE.global.exception.BadRequestException;
import com.example.PayAll_BE.global.exception.NotFoundException;
import com.example.PayAll_BE.customer.paymentDetails.PaymentDetailRepository;
import com.example.PayAll_BE.customer.payment.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReceiptService {
	private final PaymentRepository paymentRepository;
	private final PaymentDetailRepository paymentDetailRepository;

	public void uploadReceipt(ReceiptRequestDto requestDto) {
		Payment payment = paymentRepository.findById(requestDto.getPaymentId())
			.orElseThrow(() -> new NotFoundException("결제 정보를 찾을 수 없습니다."));

		if (!paymentDetailRepository.findByPaymentId(payment.getId()).isEmpty()) {
			throw new BadRequestException("이미 영수증이 등록된 결제 내역입니다.");
		}
		;

		List<PaymentDetail> products = requestDto.getProductList().stream()
			.map(product -> PaymentDetail.builder()
				.payment(payment)
				.productName(product.getProductName())
				.productPrice(product.getPrice())
				.quantity(product.getQuantity()).build())
			.toList();

		paymentDetailRepository.saveAll(products);

	}
}
