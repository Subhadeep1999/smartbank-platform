package com.smartbank.auth.dto;

import com.smartbank.auth.entity.RoleType;

import java.util.UUID;

public record UserRegistrationResponse(
        UUID id,
        String userId,
        String username,
        String bankId,
        String customerCif,
        RoleType role,
        String status
) {
}