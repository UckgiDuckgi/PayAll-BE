package com.example.PayAll_BE.mydata.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class TransactionRequestDto {
	private String orgCode;
	private String accountNum;
	private Timestamp fromDate;
	private Timestamp toDate;
	private String nextPage;
	private int limit;
}
