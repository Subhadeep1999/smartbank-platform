package com.smartbank.auth.dto;

import java.util.List;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String userId,
        String username,
        String bankId,
        String role,
        List<String> permissions
) {
}