package com.bank.transferservice.service;

import com.bank.transferservice.client.*;
import com.bank.transferservice.model.dto.*;
import com.bank.transferservice.model.entity.Transfer;
import com.bank.transferservice.model.entity.TransferStatus;
import com.bank.transferservice.model.graph.TransferNode;
import com.bank.transferservice.repository.TransferGraphRepository;
import com.bank.transferservice.repository.TransferRepository;
import com.bank.transferservice.util.CorrelationIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferOrchestrator {
    
    private final AccountServiceClient accountClient;
    private final LimitServiceClient limitClient;
    private final WorkflowServiceClient workflowClient;
    private final CoreBankingServiceClient coreBankingClient;
    private final TransferRepository transferRepository;
    private final TransferGraphRepository transferGraphRepository;
    
    @Transactional
    public TransferResponse initiateTransfer(TransferRequest request, String userId, String sessionId) {
        
        String requestId = CorrelationIdUtil.getOrGenerate();
        
        log.info("Initiating transfer - RequestId: {}, SessionId: {}, User: {}", 
            requestId, sessionId, userId);
        
        // Step 1: Validate from account belongs to user
        AccountDto fromAccount = accountClient.getAccount(request.getFromAccountId());
        if (!fromAccount.getUserId().equals(userId)) {
            throw new RuntimeException("Account does not belong to user");
        }
        
        // Step 2: Check balance
        boolean hasSufficientBalance = accountClient.hasSufficientBalance(
            request.getFromAccountId(), 
            request.getAmount()
        );
        if (!hasSufficientBalance) {
            throw new RuntimeException("Insufficient balance");
        }
        
        // Step 3: Check limits
        boolean withinLimits = limitClient.checkLimit(userId, request.getAmount());
        if (!withinLimits) {
            throw new RuntimeException("Transfer amount exceeds limit");
        }
        
        // Step 4: Create transfer record
        Transfer transfer = Transfer.builder()
            .transferReference(generateReference())
            .userId(UUID.fromString(userId))
            .fromAccountId(UUID.fromString(request.getFromAccountId()))
            .toAccountId(UUID.fromString(request.getToAccountId()))
            .amount(request.getAmount())
            .currency(request.getCurrency())
            .status(TransferStatus.INITIATED)
            .description(request.getDescription())
            .sessionId(sessionId)
            .requestId(requestId)
            .workflowRequired(false)
            .build();
        
        transfer = transferRepository.save(transfer);
        
        // Step 5: Record in graph database
        TransferNode transferNode = TransferNode.builder()
            .transferId(transfer.getId().toString())
            .amount(transfer.getAmount())
            .currency(transfer.getCurrency())
            .status(transfer.getStatus().name())
            .timestamp(LocalDateTime.now())
            .description(transfer.getDescription())
            .build();
        
        transferGraphRepository.save(transferNode);
        transferGraphRepository.linkTransferToAccounts(
            transfer.getId().toString(),
            request.getFromAccountId(),
            request.getToAccountId(),
            request.getAmount().doubleValue()
        );
        transferGraphRepository.linkTransferToUser(userId, transfer.getId().toString());
        
        // Step 6: Check if workflow approval needed
        boolean needsApproval = workflowClient.checkApprovalRequired(
            request.getAmount(),
            userId
        );
        
        if (needsApproval) {
            transfer.setWorkflowRequired(true);
            transfer.setStatus(TransferStatus.PENDING_APPROVAL);
            
            Map<String, Object> workflowResponse = workflowClient.submitForApproval(
                transfer.getId().toString(),
                request.getAmount(),
                request.getDescription()
            );
            
            transfer.setWorkflowId(UUID.fromString((String) workflowResponse.get("workflowId")));
            transferRepository.save(transfer);
            
            log.info("Transfer {} submitted for approval", transfer.getTransferReference());
            
            return buildResponse(transfer, "Transfer submitted for approval");
        }
        
        // Step 7: Process directly if no approval needed
        try {
            Map<String, Object> coreBankingResponse = coreBankingClient.processTransfer(
                transfer.getId().toString(),
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount()
            );
            
            transfer.setStatus(TransferStatus.COMPLETED);
            transfer.setCompletedAt(LocalDateTime.now());
            transferRepository.save(transfer);
            
            log.info("Transfer {} completed successfully", transfer.getTransferReference());
            
            return buildResponse(transfer, "Transfer completed successfully");
            
        } catch (Exception e) {
            log.error("Transfer {} failed", transfer.getTransferReference(), e);
            transfer.setStatus(TransferStatus.FAILED);
            transferRepository.save(transfer);
            throw new RuntimeException("Transfer processing failed: " + e.getMessage());
        }
    }
    
    public TransferDto getTransfer(String id) {
        Transfer transfer = transferRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new RuntimeException("Transfer not found"));
        return convertToDto(transfer);
    }
    
    public Page<TransferDto> getUserTransfers(String userId, Pageable pageable) {
        return transferRepository.findByUserId(UUID.fromString(userId), pageable)
            .map(this::convertToDto);
    }
    
    public List<TransferDto> getTransferHistory(String userId, LocalDateTime from, LocalDateTime to) {
        return transferRepository.findByUserIdAndInitiatedAtBetween(
                UUID.fromString(userId), from, to)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    private String generateReference() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private TransferResponse buildResponse(Transfer transfer, String message) {
        return TransferResponse.builder()
            .transferId(transfer.getId().toString())
            .transferReference(transfer.getTransferReference())
            .fromAccountId(transfer.getFromAccountId().toString())
            .toAccountId(transfer.getToAccountId().toString())
            .amount(transfer.getAmount())
            .currency(transfer.getCurrency())
            .status(transfer.getStatus())
            .description(transfer.getDescription())
            .workflowRequired(transfer.getWorkflowRequired())
            .initiatedAt(transfer.getInitiatedAt())
            .message(message)
            .build();
    }
    
    private TransferDto convertToDto(Transfer transfer) {
        return TransferDto.builder()
            .id(transfer.getId().toString())
            .transferReference(transfer.getTransferReference())
            .userId(transfer.getUserId().toString())
            .fromAccountId(transfer.getFromAccountId().toString())
            .toAccountId(transfer.getToAccountId().toString())
            .amount(transfer.getAmount())
            .currency(transfer.getCurrency())
            .status(transfer.getStatus())
            .description(transfer.getDescription())
            .workflowRequired(transfer.getWorkflowRequired())
            .workflowId(transfer.getWorkflowId() != null ? transfer.getWorkflowId().toString() : null)
            .initiatedAt(transfer.getInitiatedAt())
            .completedAt(transfer.getCompletedAt())
            .build();
    }
}