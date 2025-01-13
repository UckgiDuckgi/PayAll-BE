package com.example.PayAll_BE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.PayAll_BE.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
