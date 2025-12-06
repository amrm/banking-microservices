package com.bank.corebankingservice.controller;

import com.bank.corebankingservice.model.dto.TransferProcessRequest;
import com.bank.corebankingservice.service.CoreBankingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/core-banking")
@RequiredArgsConstructor
@Slf4j
public class CoreBankingController {
    
    private final CoreBankingService coreBankingService;
    
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processTransfer(
            @Valid @RequestBody TransferProcessRequest request) {
        log.info("Process transfer request: {}", request.getTransferId());
        Map<String, Object> response = coreBankingService.processTransfer(request);
        return ResponseEntity.ok(response);
    }
}