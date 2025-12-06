package com.bank.accountservice.controller;

import com.bank.accountservice.model.dto.AccountDto;
import com.bank.accountservice.model.dto.BalanceCheckRequest;
import com.bank.accountservice.model.dto.CreateAccountRequest;
import com.bank.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    
    private final AccountService accountService;
    
    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        log.info("Create account request for user: {}", request.getUserId());
        AccountDto account = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable String id) {
        log.info("Get account request: {}", id);
        AccountDto account = accountService.getAccount(id);
        return ResponseEntity.ok(account);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDto>> getUserAccounts(@PathVariable String userId) {
        log.info("Get accounts for user: {}", userId);
        List<AccountDto> accounts = accountService.getUserAccounts(userId);
        return ResponseEntity.ok(accounts);
    }
    
    @PostMapping("/{id}/check-balance")
    public ResponseEntity<Boolean> checkBalance(
            @PathVariable String id,
            @Valid @RequestBody BalanceCheckRequest request) {
        log.info("Balance check for account: {}", id);
        boolean hasSufficient = accountService.hasSufficientBalance(id, request.getAmount());
        return ResponseEntity.ok(hasSufficient);
    }
}