package com.bank.corebankingservice.service;

import com.bank.corebankingservice.client.AccountServiceClient;
import com.bank.corebankingservice.model.dto.TransactionDto;
import com.bank.corebankingservice.model.dto.TransferProcessRequest;
import com.bank.corebankingservice.model.entity.Transaction;
import com.bank.corebankingservice.model.entity.TransactionType;
import com.bank.corebankingservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoreBankingService {
    
    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;
    
    @Transactional
    public Map<String, Object> processTransfer(TransferProcessRequest request) {
        log.info("Processing transfer in core banking: {}", request.getTransferId());
        
        try {
            // Step 1: Debit from source account
            accountServiceClient.updateBalance(
                request.getFromAccountId(),
                request.getAmount(),
                "DEBIT"
            );
            
            BigDecimal fromAccountBalance = accountServiceClient.getBalance(request.getFromAccountId());
            
            Transaction debitTransaction = Transaction.builder()
                .transferId(UUID.fromString(request.getTransferId()))
                .transactionType(TransactionType.DEBIT)
                .accountId(UUID.fromString(request.getFromAccountId()))
                .amount(request.getAmount())
                .balanceAfter(fromAccountBalance)
                .coreBankingRef(generateCoreBankingRef())
                .build();
            
            transactionRepository.save(debitTransaction);
            
            // Step 2: Credit to destination account
            accountServiceClient.updateBalance(
                request.getToAccountId(),
                request.getAmount(),
                "CREDIT"
            );
            
            BigDecimal toAccountBalance = accountServiceClient.getBalance(request.getToAccountId());
            
            Transaction creditTransaction = Transaction.builder()
                .transferId(UUID.fromString(request.getTransferId()))
                .transactionType(TransactionType.CREDIT)
                .accountId(UUID.fromString(request.getToAccountId()))
                .amount(request.getAmount())
                .balanceAfter(toAccountBalance)
                .coreBankingRef(generateCoreBankingRef())
                .build();
            
            transactionRepository.save(creditTransaction);
            
            log.info("Transfer {} processed successfully in core banking", request.getTransferId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transferId", request.getTransferId());
            response.put("debitTransactionId", debitTransaction.getId().toString());
            response.put("creditTransactionId", creditTransaction.getId().toString());
            response.put("message", "Transfer processed successfully");
            
            return response;
            
        } catch (Exception e) {
            log.error("Error processing transfer {}", request.getTransferId(), e);
            throw new RuntimeException("Transfer processing failed: " + e.getMessage());
        }
    }
    
    private String generateCoreBankingRef() {
        return "CBR" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}   