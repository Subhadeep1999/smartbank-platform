package com.smartbank.transaction.entity;

public enum OutboxEventStatus {

    PENDING,
    PUBLISHED,
    FAILED
}