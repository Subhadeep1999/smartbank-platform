package com.smartbank.account.repository;

import com.smartbank.account.entity.Account;
import com.smartbank.account.entity.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    Page<Account> findByCifId(String cifId, Pageable pageable);

    Page<Account> findByStatus(AccountStatus status, Pageable pageable);

    Page<Account> findByCifIdAndStatus(
            String cifId,
            AccountStatus status,
            Pageable pageable
    );

    @Query(
            value = "SELECT nextval('account_schema.account_number_seq')",
            nativeQuery = true
    )
    Long getNextAccountNumber();
}