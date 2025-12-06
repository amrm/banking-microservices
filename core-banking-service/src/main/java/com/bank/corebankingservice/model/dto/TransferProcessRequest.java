package com.bank.corebankingservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferProcessRequest {
    
    @NotBlank(message = "Transfer ID is required")
    private String transferId;
    
    @NotBlank(message = "From account ID is required")
    private String fromAccountId;
    
    @NotBlank(message = "To account ID is required")
    private String toAccountId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}