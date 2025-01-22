package com.example.PayAll_BE.customer.store;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PayAll_BE.customer.store.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
