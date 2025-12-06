package com.bank.limitservice.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.bank.limitservice.model.entity.LimitType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitDto {
    private String id;
    private String userId;
    private LimitType limitType;
    private BigDecimal maxAmount;
    private String currency;
    private BigDecimal currentUtilization;
    private BigDecimal remainingAmount;
    private LocalDate resetDate;
}