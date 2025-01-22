package com.example.PayAll_BE.global.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult {
	private int code;
	private String status;
	private String message;
	private Object data;

	public ApiResult(int code, String status, String message) {
		this.code = code;
		this.message = message;
		this.status = status;
	}
}
