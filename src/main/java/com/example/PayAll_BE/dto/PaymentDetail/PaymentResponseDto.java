package com.example.PayAll_BE.dto.PaymentDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.example.PayAll_BE.dto.PaymentDetail.PaymentDetailDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {
	private String bankName;           // 은행명
	private String accountName;        // 계좌명
	private String accountNumber;      // 계좌번호
	private Long balance;              // 잔액
	private Long paymentPrice;         // 결제 금액
	private String category;           // 결제 카테고리
	private String paymentType;        // 결제 타입 (온라인/오프라인)
	private LocalDateTime paymentTime; // 결제 시간
	private String paymentPlace;       // 결제처
	private List<PaymentDetailDto> paymentDetail; // 결제 상세 내역 리스트
}
