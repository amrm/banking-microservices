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
public class WorkflowServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${services.workflow}")
    private String workflowServiceUrl;
    
    @CircuitBreaker(name = "workflowService", fallbackMethod = "checkApprovalFallback")
    public Boolean checkApprovalRequired(BigDecimal amount, String userId) {
        log.info("Checking if approval required for amount: {}", amount);
        return webClientBuilder.build()
            .post()
            .uri(workflowServiceUrl + "/api/workflows/check-approval")
            .bodyValue(Map.of("amount", amount, "userId", userId))
            .retrieve()
            .bodyToMono(Boolean.class)
            .block();
    }
    
    @CircuitBreaker(name = "workflowService", fallbackMethod = "submitForApprovalFallback")
    public Map<String, Object> submitForApproval(String transferId, BigDecimal amount, String description) {
        log.info("Submitting transfer {} for approval", transferId);
        return webClientBuilder.build()
            .post()
            .uri(workflowServiceUrl + "/api/workflows")
            .bodyValue(Map.of(
                "transferId", transferId,
                "amount", amount,
                "description", description != null ? description : ""
            ))
            .retrieve()
            .bodyToMono(Map.class)
            .block();
    }
    
    private Boolean checkApprovalFallback(BigDecimal amount, String userId, Exception ex) {
        log.error("Workflow service fallback triggered for approval check", ex);
        // Fail safe: require approval if workflow service is down for amounts > 10000
        return amount.compareTo(BigDecimal.valueOf(10000)) > 0;
    }
    
    private Map<String, Object> submitForApprovalFallback(String transferId, BigDecimal amount, 
                                                          String description, Exception ex) {
        log.error("Workflow service fallback triggered for submission", ex);
        throw new RuntimeException("Workflow service unavailable");
    }
}