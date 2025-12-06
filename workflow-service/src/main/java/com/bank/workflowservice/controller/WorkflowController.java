package com.bank.workflowservice.controller;

import com.bank.workflowservice.model.dto.ApprovalRequest;
import com.bank.workflowservice.model.dto.WorkflowDto;
import com.bank.workflowservice.model.dto.WorkflowRequest;
import com.bank.workflowservice.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
@Slf4j
public class WorkflowController {
    
    private final WorkflowService workflowService;
    
    @PostMapping("/check-approval")
    public ResponseEntity<Boolean> checkApprovalRequired(@RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String userId = (String) request.get("userId");
        
        log.info("Check approval request for amount: {}", amount);
        boolean required = workflowService.checkApprovalRequired(amount, userId);
        return ResponseEntity.ok(required);
    }
    
    @PostMapping
    public ResponseEntity<WorkflowDto> createWorkflow(@Valid @RequestBody WorkflowRequest request) {
        log.info("Create workflow request for transfer: {}", request.getTransferId());
        WorkflowDto workflow = workflowService.createWorkflow(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(workflow);
    }
    
    @PostMapping("/{id}/approve")
    public ResponseEntity<WorkflowDto> processApproval(
            @PathVariable String id,
            @Valid @RequestBody ApprovalRequest request) {
        log.info("Process approval for workflow: {}", id);
        WorkflowDto workflow = workflowService.approveOrReject(id, request);
        return ResponseEntity.ok(workflow);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDto> getWorkflow(@PathVariable String id) {
        log.info("Get workflow: {}", id);
        WorkflowDto workflow = workflowService.getWorkflow(id);
        return ResponseEntity.ok(workflow);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<WorkflowDto>> getPendingWorkflows() {
        log.info("Get pending workflows");
        List<WorkflowDto> workflows = workflowService.getPendingWorkflows();
        return ResponseEntity.ok(workflows);
    }
}