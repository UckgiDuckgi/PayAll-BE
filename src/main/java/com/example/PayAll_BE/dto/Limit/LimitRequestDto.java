package com.example.PayAll_BE.dto.Limit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LimitRequestDto {
	private long limitPrice;
}
