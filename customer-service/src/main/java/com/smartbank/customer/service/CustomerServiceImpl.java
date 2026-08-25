package com.smartbank.customer.service;


import com.smartbank.customer.dto.*;
import com.smartbank.customer.entity.Customer;
import com.smartbank.customer.entity.CustomerStatus;
import com.smartbank.customer.exception.CustomerAlreadyExistsException;
import com.smartbank.customer.exception.ResourceNotFoundException;
import com.smartbank.customer.repository.CustomerRepository;
import com.smartbank.customer.specification.CustomerSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {

        if(customerRepository.existsByCifId(request.getCifId())) {
            throw new CustomerAlreadyExistsException(
                    "Customer already exists with CIF ID: " + request.getCifId()
            );
        }

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerAlreadyExistsException(
                    "Customer already exists with email: " + request.getEmail()
            );
        }

        Customer customer = new Customer(
                request.getCifId(),
                request.getCustomerType(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getDateOfBirth()
        );

        Customer savedCustomer = customerRepository.save(customer);

        return mapToResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(UUID id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with ID: " + id
                        )
                );

        return mapToResponse(customer);
    }

    @Cacheable(
            value = "customers",
            key = "#cifId"
    )
    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByCifId(String cifId) {
        Customer customer = customerRepository.findByCifId(cifId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with CIF ID: " + cifId
                        )
                );
        return mapToResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerPageResponse getCustomers(
            CustomerSearchCriteria criteria,
            Pageable pageable
    ) {
//        Specification<Customer> specification =
//                Specification.allOf(
//                        CustomerSpecification.hasStatus(status),
//                        CustomerSpecification.hasName(name),
//                        CustomerSpecification.hasCifId(cifId),
//                        CustomerSpecification.hasEmail(email)
//                );
        Specification<Customer> specification =
                CustomerSpecification.byCriteria(criteria);

        Page<Customer> customerPage = customerRepository.findAll(specification,pageable);
        List<CustomerResponse> customers = customerPage.map(this::mapToResponse).getContent();
        return new CustomerPageResponse(
                customers,
                customerPage.getNumber(),
                customerPage.getSize(),
                customerPage.getTotalElements(),
                customerPage.getTotalPages(),
                customerPage.isFirst(),
                customerPage.isLast()
        );
    }


    @CacheEvict(
            value = "customers",
            key = "#result.cifId"
    )
    @Override
    @Transactional
    public CustomerResponse updateCustomer(UUID customerId, UpdateCustomerRequest request) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with ID: " + customerId
                        )
                );

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setDateOfBirth(request.getDateOfBirth());

        Customer updatedCustomer = customerRepository.save(customer);

        return mapToResponse(updatedCustomer);
    }

    @Override
    @Transactional
    @CacheEvict(
            value = "customers",
            key = "#result.cifId"
    )
    public CustomerResponse deactivateCustomer(UUID customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with ID: " + customerId
                        )
                );

        if (customer.getStatus() == CustomerStatus.INACTIVE) {
            throw new IllegalStateException(
                    "Customer is already inactive"
            );
        }

        customer.setStatus(CustomerStatus.INACTIVE);

        Customer updatedCustomer =
                customerRepository.save(customer);

        return mapToResponse(updatedCustomer);
    }

    private CustomerResponse mapToResponse(Customer customer) {

        CustomerResponse response = new CustomerResponse();

        response.setId(customer.getId());
        response.setCifId(customer.getCifId());
        response.setCustomerType(customer.getCustomerType());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail());
        response.setPhoneNumber(customer.getPhoneNumber());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setStatus(customer.getStatus());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());

        return response;
    }
}
