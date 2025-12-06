package com.bank.workflowservice.model.dto;

import com.bank.workflowservice.model.entity.WorkflowStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowDto {
    private String id;
    private String transferId;
    private WorkflowStatus status;
    private BigDecimal amount;
    private String description;
    private String assignedTo;
    private String comments;
    private String approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}