package com.bank.corebankingservice.model.dto;

import com.bank.corebankingservice.model.entity.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private String id;
    private String transferId;
    private TransactionType transactionType;
    private String accountId;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String coreBankingRef;
    private LocalDateTime postedAt;
}