package com.example.PayAll_BE.mydata.dto;

import lombok.Data;

@Data
public class AccountRequestDto {
	private String orgCode;
	private String accountNum;
	private String searchTimestamp;
}
