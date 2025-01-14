package com.example.PayAll_BE.mydata.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponseDto {
	private String rspCode;
	private String rspMsg;
	private String baseDate;
	private int basicCnt;
	private List<AccountBasicInfoDto> basicList;
}
