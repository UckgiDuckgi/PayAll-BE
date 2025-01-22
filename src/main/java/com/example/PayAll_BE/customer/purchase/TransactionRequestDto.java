package com.example.PayAll_BE.customer.purchase;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionRequestDto {
	private Long price;
}
