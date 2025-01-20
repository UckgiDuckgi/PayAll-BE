package com.example.PayAll_BE.mydata.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.mydata.service.MydataService;
import com.example.PayAll_BE.service.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {
	private final MydataService mydataService;
	private final JwtService jwtService;

	@GetMapping("/mydata/test")
	public void getTest(@RequestHeader("Authorization") String token) {
		// Long userID = jwtService.extractUserId(token.replace("Bearer ", ""));
		mydataService.syncMydataInfo(token);
	}
}
