package com.bank.limitservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

import com.bank.limitservice.model.entity.LimitType;

@Data
public class CreateLimitRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Limit type is required")
    private LimitType limitType;
    
    @NotNull(message = "Max amount is required")
    @Positive(message = "Max amount must be positive")
    private BigDecimal maxAmount;
    
    @NotBlank(message = "Currency is required")
    private String currency;
}