package com.example.PayAll_BE.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.PayAll_BE.global.api.ApiResult;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
		// return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResult(404, "NOT_FOUND", e.getMessage(),
			null));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body(new ApiResult(401, "UNAUTHORIZED", e.getMessage(), null));
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<?> handleForbiddenException(ForbiddenException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResult(403, "FORBIDDEN", e.getMessage(), null));
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<?> handleBadRequestException(BadRequestException e) {
		return ResponseEntity.badRequest().body(new ApiResult(400, "BAD_REQUEST", e.getMessage(), null));
	}

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiResult> handleBaseException(BaseException e) {
		return ResponseEntity.badRequest()
			.body(new ApiResult(400, "BASE_EXCEPTION_ERROR", e.getMessage(), null));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleAllExceptions(Exception e) {
		return ResponseEntity.internalServerError()
			.body(new ApiResult(500, "INTERNAL_SERVER_ERROR", e.getMessage(), null));
	}

}
