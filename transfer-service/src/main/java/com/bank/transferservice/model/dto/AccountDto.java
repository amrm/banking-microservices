package com.bank.transferservice.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private UUID id;
    private UUID userId;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private String status;
}
