package com.bank.transferservice.model.dto;

import com.bank.transferservice.model.entity.TransferStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {
    private String transferId;
    private String transferReference;
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
    private String currency;
    private TransferStatus status;
    private String description;
    private Boolean workflowRequired;
    private LocalDateTime initiatedAt;
    private String message;
}