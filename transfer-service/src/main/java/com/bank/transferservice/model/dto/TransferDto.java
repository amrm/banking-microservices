package com.bank.transferservice.model.dto;

import com.bank.transferservice.model.entity.TransferStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {
    private String id;
    private String transferReference;
    private String userId;
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
    private String currency;
    private TransferStatus status;
    private String description;
    private Boolean workflowRequired;
    private String workflowId;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
}