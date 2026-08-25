package com.smartbank.customer.repository;

import com.smartbank.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID>,
        JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByCifId(String cifId);

    Optional<Customer> findByEmail(String email);

    boolean existsByCifId(String cifId);

    boolean existsByEmail(String email);
}