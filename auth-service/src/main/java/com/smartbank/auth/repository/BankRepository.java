package com.smartbank.auth.repository;

import com.smartbank.auth.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BankRepository extends JpaRepository<Bank, UUID> {

    Optional<Bank> findByBankId(String bankId);

    boolean existsByBankId(String bankId);
}