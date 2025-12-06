package com.bank.accountservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceCheckRequest {
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
}