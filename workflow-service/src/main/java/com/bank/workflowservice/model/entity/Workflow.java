package com.bank.workflowservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workflow {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private UUID transferId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkflowStatus status;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(length = 500)
    private String description;
    
    @Column
    private String assignedTo;
    
    @Column(columnDefinition = "TEXT")
    private String approvalRules;
    
    @Column(length = 1000)
    private String comments;
    
    @Column
    private String approvedBy;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime resolvedAt;
}