package com.smartbank.transaction.client;

import com.smartbank.transaction.exception.AccountServiceUnavailableException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.annotation.Bulkhead.Type;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class AccountServiceClient {

    private static final Logger log =
            LoggerFactory.getLogger(AccountServiceClient.class);

    private final RestClient restClient;

    public AccountServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "accountServiceFallback"
    )
    @Retry(
            name = "accountService"
    )
    @Bulkhead(
            name = "accountService",
            type = Type.SEMAPHORE,
            fallbackMethod = "bulkheadFallback"
    )
    public boolean accountExists(String accountNumber) {

        long start = System.currentTimeMillis();

        try {

            log.info(
                    ">>> Calling Account Service for account: {}",
                    accountNumber
            );

            restClient.get()
                    .uri(
                            "/api/v1/accounts/{accountNumber}",
                            accountNumber
                    )
                    .retrieve()
                    .toBodilessEntity();

            log.info(
                    "<<< Account Service responded in {} ms",
                    System.currentTimeMillis() - start
            );

            return true;

        } catch (HttpClientErrorException.NotFound ex) {

            log.info(
                    "Account not found: {}",
                    accountNumber
            );

            return false;
        }
    }

    private boolean accountServiceFallback(
            String accountNumber,
            Throwable throwable
    ) {

        throw new AccountServiceUnavailableException(
                "Account Service is currently unavailable",
                throwable
        );
    }

    private boolean bulkheadFallback(
            String accountNumber,
            Throwable throwable
    ) {

        log.warn(
                "!!! Account Service call failed - account: {} - exception: {}",
                accountNumber,
                throwable.getClass().getSimpleName()
        );

        throw new AccountServiceUnavailableException(
                "Account Service is currently unavailable",
                throwable
        );
    }
}