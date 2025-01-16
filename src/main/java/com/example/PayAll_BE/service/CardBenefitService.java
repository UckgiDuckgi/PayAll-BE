package com.example.PayAll_BE.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.StoreStatisticsDto;
import com.example.PayAll_BE.entity.Product;
import com.example.PayAll_BE.entity.Benefit;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.entity.enums.Category;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.repository.CardBenefitRepository;
import com.example.PayAll_BE.repository.CardRepository;
import com.example.PayAll_BE.repository.PaymentRepository;
import com.example.PayAll_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardBenefitService {

	private final PaymentRepository paymentRepository;
	private final CardBenefitRepository cardBenefitRepository;
	private final CardRepository cardRepository;
	private final UserRepository userRepository;

}
