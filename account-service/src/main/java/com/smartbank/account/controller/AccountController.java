package com.smartbank.account.controller;

import com.smartbank.account.dto.AccountResponse;
import com.smartbank.account.dto.CreateAccountRequest;
import com.smartbank.account.exception.RateLimitExceededException;
import com.smartbank.account.service.AccountService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RateLimiter(
            name = "accountApi",
            fallbackMethod = "rateLimitFallback"
    )
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ) {

        AccountResponse response =
                accountService.createAccount(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    private ResponseEntity<AccountResponse> rateLimitFallback(
            CreateAccountRequest request,
            RequestNotPermitted exception
    ) {
        throw new RateLimitExceededException(
                "Too many requests. Please try again later.",
                exception
        );
    }


    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByAccountNumber(
            @PathVariable String accountNumber
    ) {

        AccountResponse response =
                accountService.getAccountByAccountNumber(accountNumber);

        return ResponseEntity.ok(response);
    }
}