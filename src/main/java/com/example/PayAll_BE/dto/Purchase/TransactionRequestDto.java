package com.example.PayAll_BE.dto.Purchase;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionRequestDto {
	private Long price;
}
