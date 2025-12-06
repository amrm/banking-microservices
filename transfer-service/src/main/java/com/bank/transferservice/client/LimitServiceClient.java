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
public class LimitServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${services.limit}")
    private String limitServiceUrl;
    
    @CircuitBreaker(name = "limitService", fallbackMethod = "checkLimitFallback")
    public Boolean checkLimit(String userId, BigDecimal amount) {
        log.info("Checking limit for user: {}, amount: {}", userId, amount);
        return webClientBuilder.build()
            .post()
            .uri(limitServiceUrl + "/api/limits/check")
            .bodyValue(Map.of("userId", userId, "amount", amount))
            .retrieve()
            .bodyToMono(Boolean.class)
            .block();
    }
    
    private Boolean checkLimitFallback(String userId, BigDecimal amount, Exception ex) {
        log.error("Limit service fallback triggered", ex);
        // Fail safe: deny transaction if limit service is down
        return false;
    }
}