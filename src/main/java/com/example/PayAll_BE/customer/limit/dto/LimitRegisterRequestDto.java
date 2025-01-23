package com.example.PayAll_BE.customer.limit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LimitRegisterRequestDto {
	private long limitPrice;
}
