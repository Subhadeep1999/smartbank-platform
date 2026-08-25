package com.smartbank.customer.controller;

import com.smartbank.customer.dto.*;
import com.smartbank.customer.entity.CustomerStatus;
import com.smartbank.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustoer(
            @Valid @RequestBody CreateCustomerRequest request
    ){
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @PathVariable UUID id
    ) {

        CustomerResponse response = customerService.getCustomerById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cif/{cifId}")
    public ResponseEntity<CustomerResponse> getCustomerByCifId(
            @PathVariable String cifId
    ) throws InterruptedException {

        //Thread.sleep(10000);
        CustomerResponse response = customerService.getCustomerByCifId(cifId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CustomerPageResponse> getCustomers(
            @RequestParam(required = false) CustomerStatus status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String cifId,
            @RequestParam(required = false) String email,
            Pageable pageable
    ) {

        CustomerSearchCriteria criteria = new CustomerSearchCriteria();

        criteria.setStatus(status);
        criteria.setName(name);
        criteria.setCifId(cifId);
        criteria.setEmail(email);

        CustomerPageResponse response =
                customerService.getCustomers(
                        criteria,
                        pageable
                );

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable UUID customerId,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        CustomerResponse response = customerService.updateCustomer(customerId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{customerId}/deactivate")
    public ResponseEntity<CustomerResponse> deactivateCustomer(
            @PathVariable UUID customerId
    ) {

        CustomerResponse response =
                customerService.deactivateCustomer(customerId);

        return ResponseEntity.ok(response);
    }
}
