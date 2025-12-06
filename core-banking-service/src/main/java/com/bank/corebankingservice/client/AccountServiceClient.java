package com.bank.corebankingservice.client;

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
    
    public void updateBalance(String accountId, BigDecimal amount, String transactionType) {
        log.info("Updating balance for account: {}, amount: {}, type: {}", 
            accountId, amount, transactionType);
        
        webClientBuilder.build()
            .post()
            .uri(accountServiceUrl + "/api/accounts/" + accountId + "/update-balance")
            .bodyValue(Map.of("amount", amount, "transactionType", transactionType))
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }
    
    public BigDecimal getBalance(String accountId) {
        log.info("Getting balance for account: {}", accountId);
        
        Map<String, Object> response = webClientBuilder.build()
            .get()
            .uri(accountServiceUrl + "/api/accounts/" + accountId + "/balance")
            .retrieve()
            .bodyToMono(Map.class)
            .block();
        
        return new BigDecimal(response.get("balance").toString());
    }
}