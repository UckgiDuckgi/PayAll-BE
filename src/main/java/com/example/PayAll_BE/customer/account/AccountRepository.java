package com.example.PayAll_BE.customer.account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	List<Account> findAllByUserId(Long id);

	Optional<Account> findByUserIdAndAccountNumber(Long userId, String accountNum);
}
