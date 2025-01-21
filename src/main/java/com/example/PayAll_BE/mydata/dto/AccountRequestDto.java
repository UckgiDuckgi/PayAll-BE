package com.example.PayAll_BE.mydata.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountRequestDto {
	private String orgCode;
	private String accountNum;
	private String searchTimestamp;
}
