package com.bank.transferservice.client;

import com.bank.transferservice.model.dto.AccountDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${services.account}")
    private String accountServiceUrl;
    
    @CircuitBreaker(name = "accountService", fallbackMethod = "getAccountFallback")
    public AccountDto getAccount(String accountId) {
        log.info("Calling account service for account: {}", accountId);
        return webClientBuilder.build()
            .get()
            .uri(accountServiceUrl + "/api/accounts/" + accountId)
            .retrieve()
            .bodyToMono(AccountDto.class)
            .block();
    }
    
    @CircuitBreaker(name = "accountService", fallbackMethod = "checkBalanceFallback")
    public Boolean hasSufficientBalance(String accountId, BigDecimal amount) {
        log.info("Checking balance for account: {}, amount: {}", accountId, amount);
        return webClientBuilder.build()
            .post()
            .uri(accountServiceUrl + "/api/accounts/" + accountId + "/check-balance")
            .bodyValue(Map.of("amount", amount))
            .retrieve()
            .bodyToMono(Boolean.class)
            .block();
    }
    
    private AccountDto getAccountFallback(String accountId, Exception ex) {
        log.error("Account service fallback triggered for: {}", accountId, ex);
        throw new RuntimeException("Account service unavailable");
    }
    
    private Boolean checkBalanceFallback(String accountId, BigDecimal amount, Exception ex) {
        log.error("Balance check fallback triggered", ex);
        throw new RuntimeException("Account service unavailable");
    }
}