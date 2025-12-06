package com.bank.accountservice.model.dto;

import com.bank.accountservice.model.entity.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Account type is required")
    private AccountType accountType;
    
    @NotNull(message = "Initial balance is required")
    private BigDecimal initialBalance;
    
    @NotBlank(message = "Currency is required")
    private String currency;
}