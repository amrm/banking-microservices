package com.bank.transferservice.client;

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
public class CoreBankingServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${services.core-banking}")
    private String coreBankingServiceUrl;
    
    @CircuitBreaker(name = "coreBankingService", fallbackMethod = "processTransferFallback")
    public Map<String, Object> processTransfer(String transferId, String fromAccountId, 
                                               String toAccountId, BigDecimal amount) {
        log.info("Processing transfer in core banking: {}", transferId);
        return webClientBuilder.build()
            .post()
            .uri(coreBankingServiceUrl + "/api/core-banking/process")
            .bodyValue(Map.of(
                "transferId", transferId,
                "fromAccountId", fromAccountId,
                "toAccountId", toAccountId,
                "amount", amount
            ))
            .retrieve()
            .bodyToMono(Map.class)
            .block();
    }
    
    private Map<String, Object> processTransferFallback(String transferId, String fromAccountId,
                                                        String toAccountId, BigDecimal amount, 
                                                        Exception ex) {
        log.error("Core banking service fallback triggered", ex);
        throw new RuntimeException("Core banking service unavailable");
    }
}