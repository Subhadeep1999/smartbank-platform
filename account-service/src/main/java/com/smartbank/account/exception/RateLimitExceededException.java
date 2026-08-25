package com.smartbank.account.exception;

public class RateLimitExceededException extends RuntimeException{

    public RateLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
