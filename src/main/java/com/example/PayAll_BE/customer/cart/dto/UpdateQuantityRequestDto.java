package com.example.PayAll_BE.customer.cart.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateQuantityRequestDto {
	private int quantity;
}
