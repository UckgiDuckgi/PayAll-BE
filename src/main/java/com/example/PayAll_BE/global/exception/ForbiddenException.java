package com.example.PayAll_BE.global.exception;

public class ForbiddenException extends RuntimeException {
	public ForbiddenException(String message) {
		super(message);
	}
}
