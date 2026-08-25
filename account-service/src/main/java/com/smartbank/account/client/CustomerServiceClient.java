package com.smartbank.account.client;

import com.smartbank.account.exception.CustomerServiceUnavailableException;
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
public class CustomerServiceClient {

    private static final Logger log =
            LoggerFactory.getLogger(CustomerServiceClient.class);

    private final RestClient restClient;

    public CustomerServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @CircuitBreaker(
            name = "customerService",
            fallbackMethod = "customerServiceFallback"
    )
    @Retry(
            name = "customerService"
    )
    @Bulkhead(
            name = "customerService",
            type = Type.SEMAPHORE,
            fallbackMethod = "bulkheadFallback"
    )
    public boolean customerExists(String cifId) {

        long start = System.currentTimeMillis();

        try {

            log.info(">>> Calling Customer Service for CIF: {}", cifId);

            restClient.get()
                    .uri(
                            "/api/v1/customers/cif/{cifId}",
                            cifId
                    )
                    .retrieve()
                    .toBodilessEntity();

            log.info(
                    "<<< Customer Service responded in {} ms",
                    System.currentTimeMillis() - start
            );

            return true;

        } catch (HttpClientErrorException.NotFound ex) {

            log.error(
                    "XXX Customer Service failed after {} ms. Exception: {}",
                    System.currentTimeMillis() - start,
                    ex.getClass().getName(),
                    ex
            );

            return false;
        }
    }

    public boolean customerServiceFallback(
            String cifId,
            Throwable throwable
    ) {

        log.error(
                "!!! CIRCUIT BREAKER FALLBACK - CIF: {} - Exception: {}",
                cifId,
                throwable.getClass().getSimpleName()
        );

        throw new CustomerServiceUnavailableException(
                "Customer Service is currently unavailable",
                throwable
        );
    }

    public boolean bulkheadFallback(
            String cifId,
            Throwable throwable
    ) {

        if (throwable instanceof io.github.resilience4j.bulkhead.BulkheadFullException) {

            log.warn(
                    "!!! BULKHEAD REJECTED - CIF: {}",
                    cifId
            );

            throw new CustomerServiceUnavailableException(
                    "Resources unavailable. Please try again later.",
                    throwable
            );
        }

        log.warn(
                "!!! Customer Service call failed - CIF: {} - Exception: {}",
                cifId,
                throwable.getClass().getSimpleName()
        );

        throw new CustomerServiceUnavailableException(
                "Customer Service is currently unavailable",
                throwable
        );
    }
}