package com.smartbank.account.dto;

import com.smartbank.account.entity.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateAccountRequest {

    @NotBlank(message = "CIF ID is required")
    @Size(max = 30, message = "CIF ID must not exceed 30 characters")
    private String cifId;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotBlank(message = "Currency is required")
    @Pattern(
            regexp = "^[A-Z]{3}$",
            message = "Currency must be a valid 3-letter uppercase code"
    )
    private String currency;

    public String getCifId() {
        return cifId;
    }

    public void setCifId(String cifId) {
        this.cifId = cifId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}