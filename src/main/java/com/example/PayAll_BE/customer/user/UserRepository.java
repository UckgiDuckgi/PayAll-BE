package com.example.PayAll_BE.customer.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.PayAll_BE.customer.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByAuthId(String authId);
}
