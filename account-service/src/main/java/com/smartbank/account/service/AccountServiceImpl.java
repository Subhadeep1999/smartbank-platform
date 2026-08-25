package com.smartbank.account.service;

import com.smartbank.account.client.CustomerServiceClient;
import com.smartbank.account.dto.AccountResponse;
import com.smartbank.account.dto.CreateAccountRequest;
import com.smartbank.account.entity.Account;
import com.smartbank.account.exception.CustomerNotFoundException;
import com.smartbank.account.exception.ResourceNotFoundException;
import com.smartbank.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerServiceClient customerServiceClient;

    public AccountServiceImpl(AccountRepository accountRepository, CustomerServiceClient customerServiceClient) {
        this.accountRepository = accountRepository;
        this.customerServiceClient = customerServiceClient;
    }

    @Override
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {

        boolean customerExists = customerServiceClient.customerExists(request.getCifId());

        if(!customerExists){
            throw new CustomerNotFoundException(
                    "Customer not found with CIF: " + request.getCifId()
            );
        }

        String accountNumber = generateAccountNumber();

        Account account = new Account(
                accountNumber,
                request.getCifId(),
                request.getAccountType(),
                request.getCurrency()
        );

        account.setAccountNumber(accountNumber);
        account.setCifId(request.getCifId());
        account.setAccountType(request.getAccountType());
        account.setCurrency(request.getCurrency());
        account.setBalance(BigDecimal.ZERO);

        Account savedAccount = accountRepository.save(account);

        return mapToResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found with account number: "
                                        + accountNumber
                        )
                );

        return mapToResponse(account);
    }

    private String generateAccountNumber() {

        Long sequenceValue = accountRepository.getNextAccountNumber();

        return "50" + sequenceValue;
    }

    private AccountResponse mapToResponse(Account account) {

        AccountResponse response = new AccountResponse();

        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setCifId(account.getCifId());
        response.setAccountType(account.getAccountType());
        response.setCurrency(account.getCurrency());
        response.setBalance(account.getBalance());
        response.setStatus(account.getStatus());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());

        return response;
    }
}