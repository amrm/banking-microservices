package com.bank.workflowservice.service;

import com.bank.workflowservice.model.dto.ApprovalRequest;
import com.bank.workflowservice.model.dto.WorkflowDto;
import com.bank.workflowservice.model.dto.WorkflowRequest;
import com.bank.workflowservice.model.entity.Workflow;
import com.bank.workflowservice.model.entity.WorkflowStatus;
import com.bank.workflowservice.model.graph.WorkflowNode;
import com.bank.workflowservice.repository.WorkflowGraphRepository;
import com.bank.workflowservice.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {
    
    private final WorkflowRepository workflowRepository;
    private final WorkflowGraphRepository workflowGraphRepository;
    
    @Value("${workflow.approval-threshold}")
    private BigDecimal approvalThreshold;
    
    public boolean checkApprovalRequired(BigDecimal amount, String userId) {
        log.info("Checking if approval required for amount: {}", amount);
        boolean required = amount.compareTo(approvalThreshold) > 0;
        log.info("Approval required: {}", required);
        return required;
    }
    
    @Transactional
    public WorkflowDto createWorkflow(WorkflowRequest request) {
        log.info("Creating workflow for transfer: {}", request.getTransferId());
        
        Workflow workflow = Workflow.builder()
            .transferId(UUID.fromString(request.getTransferId()))
            .status(WorkflowStatus.PENDING)
            .amount(request.getAmount())
            .description(request.getDescription())
            .assignedTo("ADMIN") // In real scenario, assign based on rules
            .approvalRules("Amount exceeds threshold")
            .build();
        
        workflow = workflowRepository.save(workflow);
        
        // Create workflow node in Neo4j
        WorkflowNode workflowNode = WorkflowNode.builder()
            .workflowId(workflow.getId().toString())
            .status(workflow.getStatus().name())
            .assignedTo(workflow.getAssignedTo())
            .createdAt(LocalDateTime.now())
            .build();
        
        workflowGraphRepository.save(workflowNode);
        workflowGraphRepository.linkWorkflowToTransfer(
            request.getTransferId(),
            workflow.getId().toString()
        );
        
        log.info("Workflow created successfully: {}", workflow.getId());
        
        return convertToDto(workflow);
    }
    
    @Transactional
    public WorkflowDto approveOrReject(String workflowId, ApprovalRequest request) {
        log.info("Processing approval for workflow: {}, approved: {}", 
            workflowId, request.getApproved());
        
        Workflow workflow = workflowRepository.findById(UUID.fromString(workflowId))
            .orElseThrow(() -> new RuntimeException("Workflow not found"));
        
        if (workflow.getStatus() != WorkflowStatus.PENDING) {
            throw new RuntimeException("Workflow already processed");
        }
        
        workflow.setStatus(request.getApproved() ? WorkflowStatus.APPROVED : WorkflowStatus.REJECTED);
        workflow.setApprovedBy(request.getApproverId());
        workflow.setComments(request.getComments());
        workflow.setResolvedAt(LocalDateTime.now());
        
        workflow = workflowRepository.save(workflow);
        
        // Update graph
        if (request.getApproved()) {
            workflowGraphRepository.linkApproverToWorkflow(
                request.getApproverId(),
                workflowId
            );
        }
        
        log.info("Workflow {} processed successfully: {}", 
            workflowId, workflow.getStatus());
        
        return convertToDto(workflow);
    }
    
    public WorkflowDto getWorkflow(String id) {
        Workflow workflow = workflowRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new RuntimeException("Workflow not found"));
        return convertToDto(workflow);
    }
    
    public List<WorkflowDto> getPendingWorkflows() {
        return workflowRepository.findByStatus(WorkflowStatus.PENDING)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    private WorkflowDto convertToDto(Workflow workflow) {
        return WorkflowDto.builder()
            .id(workflow.getId().toString())
            .transferId(workflow.getTransferId().toString())
            .status(workflow.getStatus())
            .amount(workflow.getAmount())
            .description(workflow.getDescription())
            .assignedTo(workflow.getAssignedTo())
            .comments(workflow.getComments())
            .approvedBy(workflow.getApprovedBy())
            .createdAt(workflow.getCreatedAt())
            .resolvedAt(workflow.getResolvedAt())
            .build();
    }
}