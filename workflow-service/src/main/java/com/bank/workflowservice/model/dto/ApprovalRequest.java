package com.bank.workflowservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApprovalRequest {
    
    @NotNull(message = "Approved flag is required")
    private Boolean approved;
    
    @NotBlank(message = "Approver ID is required")
    private String approverId;
    
    private String comments;
}