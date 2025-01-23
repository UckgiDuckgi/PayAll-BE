package com.example.PayAll_BE.global.mydata.dto;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionRequestDto {
	private String orgCode;
	private String accountNum;
	private Timestamp fromDate;
	private Timestamp toDate;
	private String nextPage;
	private int limit;
}
