package com.smartbank.customer.service;

import com.smartbank.customer.dto.*;
import com.smartbank.customer.entity.CustomerStatus;
import org.springframework.data.domain.Pageable;


import java.util.UUID;

public interface CustomerService {

    CustomerResponse createCustomer(CreateCustomerRequest request);
    CustomerResponse getCustomerById(UUID id);
    CustomerResponse getCustomerByCifId(String cifId);
    CustomerPageResponse getCustomers(
            CustomerSearchCriteria criteria,
            Pageable pageable
    );
    CustomerResponse updateCustomer(UUID customerId, UpdateCustomerRequest request);
    CustomerResponse deactivateCustomer(UUID customerId);
}
