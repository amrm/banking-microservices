package com.bank.corebankingservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private UUID transferId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType transactionType;
    
    @Column(nullable = false)
    private UUID accountId;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;
    
    @Column(length = 100)
    private String coreBankingRef;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime postedAt;
}