package com.bank.accountservice.service;

import com.bank.accountservice.model.dto.AccountDto;
import com.bank.accountservice.model.dto.CreateAccountRequest;
import com.bank.accountservice.model.entity.Account;
import com.bank.accountservice.model.entity.AccountStatus;
import com.bank.accountservice.model.graph.AccountNode;
import com.bank.accountservice.repository.AccountGraphRepository;
import com.bank.accountservice.repository.AccountRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final AccountGraphRepository accountGraphRepository;
    
    @Transactional
    public AccountDto createAccount(CreateAccountRequest request) {
        log.info("Creating account for user: {}", request.getUserId());
        
        Account account = Account.builder()
            .userId(UUID.fromString(request.getUserId()))
            .accountNumber(generateAccountNumber())
            .accountType(request.getAccountType())
            .balance(request.getInitialBalance())
            .currency(request.getCurrency())
            .status(AccountStatus.ACTIVE)
            .build();
        
        account = accountRepository.save(account);
        
        // Create account node in Neo4j
        AccountNode accountNode = AccountNode.builder()
            .accountId(account.getId().toString())
            .accountNumber(account.getAccountNumber())
            .accountType(account.getAccountType().name())
            .currentBalance(account.getBalance())
            .build();
        
        accountGraphRepository.save(accountNode);
        accountGraphRepository.linkAccountToUser(request.getUserId(), account.getId().toString());
        
        log.info("Account created successfully: {}", account.getAccountNumber());
        
        return convertToDto(account);
    }
    
    @CircuitBreaker(name = "accountService", fallbackMethod = "getAccountFallback")
    public AccountDto getAccount(String id) {
        log.info("Fetching account: {}", id);
        Account account = accountRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new RuntimeException("Account not found"));
        return convertToDto(account);
    }
    
    public List<AccountDto> getUserAccounts(String userId) {
        log.info("Fetching accounts for user: {}", userId);
        return accountRepository.findByUserId(UUID.fromString(userId))
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public boolean hasSufficientBalance(String accountId, BigDecimal amount) {
        log.info("Checking balance for account: {}, amount: {}", accountId, amount);
        Account account = accountRepository.findById(UUID.fromString(accountId))
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        boolean hasSufficient = account.getBalance().compareTo(amount) >= 0;
        log.info("Balance check result: {}", hasSufficient);
        return hasSufficient;
    }
    
    @Transactional
    public void updateBalance(String accountId, BigDecimal amount, String transactionType) {
        log.info("Updating balance for account: {}, amount: {}, type: {}", 
            accountId, amount, transactionType);
        
        Account account = accountRepository.findByIdForUpdate(UUID.fromString(accountId))
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if ("DEBIT".equals(transactionType)) {
            account.setBalance(account.getBalance().subtract(amount));
        } else if ("CREDIT".equals(transactionType)) {
            account.setBalance(account.getBalance().add(amount));
        }
        
        accountRepository.save(account);
        
        // Update graph
        AccountNode accountNode = accountGraphRepository.findByAccountId(accountId)
            .orElseThrow(() -> new RuntimeException("Account node not found"));
        accountNode.setCurrentBalance(account.getBalance());
        accountGraphRepository.save(accountNode);
        
        log.info("Balance updated successfully. New balance: {}", account.getBalance());
    }
    
    private String generateAccountNumber() {
        Random random = new Random();
        return String.format("%010d", random.nextInt(1000000000));
    }
    
    private AccountDto convertToDto(Account account) {
        return AccountDto.builder()
            .id(account.getId().toString())
            .userId(account.getUserId().toString())
            .accountNumber(account.getAccountNumber())
            .accountType(account.getAccountType())
            .balance(account.getBalance())
            .currency(account.getCurrency())
            .status(account.getStatus())
            .createdAt(account.getCreatedAt())
            .build();
    }
    
    private AccountDto getAccountFallback(String id, Exception ex) {
        log.error("Fallback triggered for account: {}", id, ex);
        throw new RuntimeException("Account service temporarily unavailable");
    }
}