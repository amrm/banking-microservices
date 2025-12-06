package com.bank.limitservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "limits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Limit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private UUID userId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LimitType limitType;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal maxAmount;
    
    @Column(nullable = false, length = 3)
    private String currency;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentUtilization;
    
    @Column(nullable = false)
    private LocalDate resetDate;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}