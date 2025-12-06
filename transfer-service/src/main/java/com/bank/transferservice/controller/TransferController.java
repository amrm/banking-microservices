package com.bank.transferservice.controller;

import com.bank.transferservice.model.dto.TransferDto;
import com.bank.transferservice.model.dto.TransferRequest;
import com.bank.transferservice.model.dto.TransferResponse;
import com.bank.transferservice.service.TransferOrchestrator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Slf4j
public class TransferController {
    
    private final TransferOrchestrator transferOrchestrator;
    
    @PostMapping("/initiate")
    public ResponseEntity<TransferResponse> initiateTransfer(
            @Valid @RequestBody TransferRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest) {
        
        String userId = userDetails.getUsername(); // In real scenario, get user ID
        String sessionId = httpRequest.getSession().getId();
        
        log.info("Transfer initiation request from user: {}", userId);
        TransferResponse response = transferOrchestrator.initiateTransfer(request, userId, sessionId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TransferDto> getTransfer(@PathVariable String id) {
        log.info("Get transfer request: {}", id);
        TransferDto transfer = transferOrchestrator.getTransfer(id);
        return ResponseEntity.ok(transfer);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<TransferDto>> getUserTransfers(
            @PathVariable String userId,
            Pageable pageable) {
        log.info("Get transfers for user: {}", userId);
        Page<TransferDto> transfers = transferOrchestrator.getUserTransfers(userId, pageable);
        return ResponseEntity.ok(transfers);
    }
    
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<TransferDto>> getTransferHistory(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        log.info("Get transfer history for user: {} from {} to {}", userId, from, to);
        List<TransferDto> history = transferOrchestrator.getTransferHistory(userId, from, to);
        return ResponseEntity.ok(history);
    }
}