package com.bank.accountservice.model.dto;

import com.bank.accountservice.model.entity.AccountStatus;
import com.bank.accountservice.model.entity.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private String id;
    private String userId;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private String currency;
    private AccountStatus status;
    private LocalDateTime createdAt;
}