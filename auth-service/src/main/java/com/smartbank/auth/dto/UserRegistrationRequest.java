package com.smartbank.auth.dto;

import com.smartbank.auth.entity.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 5, max = 100, message = "Username must be between 5 and 100 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password,

        @NotBlank(message = "Bank ID is required")
        @Size(max = 20, message = "Bank ID cannot exceed 20 characters")
        String bankId,

        @Size(max = 30, message = "CIF cannot exceed 30 characters")
        String customerCif,

        @NotNull(message = "Role is required")
        RoleType role
) {
}