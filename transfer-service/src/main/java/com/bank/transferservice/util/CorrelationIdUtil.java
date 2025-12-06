package com.bank.transferservice.util;

import org.slf4j.MDC;

import java.util.UUID;

public class CorrelationIdUtil {
    
    private static final String CORRELATION_ID_KEY = "correlationId";
    
    public static String getOrGenerate() {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
            MDC.put(CORRELATION_ID_KEY, correlationId);
        }
        return correlationId;
    }
    
    public static void set(String correlationId) {
        MDC.put(CORRELATION_ID_KEY, correlationId);
    }
    
    public static void clear() {
        MDC.remove(CORRELATION_ID_KEY);
    }
}