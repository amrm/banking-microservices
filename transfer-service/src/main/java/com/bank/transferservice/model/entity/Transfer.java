package com.bank.transferservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transfer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String transferReference;
    
    @Column(nullable = false)
    private UUID userId;
    
    @Column(nullable = false)
    private UUID fromAccountId;
    
    @Column(nullable = false)
    private UUID toAccountId;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false, length = 3)
    private String currency;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransferStatus status;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private Boolean workflowRequired;
    
    @Column
    private UUID workflowId;
    
    @Column(length = 50)
    private String sessionId;
    
    @Column(length = 50)
    private String requestId;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime initiatedAt;
    
    @Column
    private LocalDateTime completedAt;
}