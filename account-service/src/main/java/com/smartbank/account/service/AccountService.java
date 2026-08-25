package com.smartbank.account.service;

import com.smartbank.account.dto.AccountResponse;
import com.smartbank.account.dto.CreateAccountRequest;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);

    AccountResponse getAccountByAccountNumber(String accountNumber);
}