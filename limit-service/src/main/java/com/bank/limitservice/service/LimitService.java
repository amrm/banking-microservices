package com.bank.limitservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.limitservice.model.dto.CreateLimitRequest;
import com.bank.limitservice.model.dto.LimitDto;
import com.bank.limitservice.model.entity.Limit;
import com.bank.limitservice.model.entity.LimitType;
import com.bank.limitservice.repository.LimitRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LimitService {
    
    private final LimitRepository limitRepository;
    
    @Transactional
    public LimitDto createLimit(CreateLimitRequest request) {
        log.info("Creating limit for user: {}, type: {}", request.getUserId(), request.getLimitType());
        
        Limit limit = Limit.builder()
            .userId(UUID.fromString(request.getUserId()))
            .limitType(request.getLimitType())
            .maxAmount(request.getMaxAmount())
            .currency(request.getCurrency())
            .currentUtilization(BigDecimal.ZERO)
            .resetDate(calculateResetDate(request.getLimitType()))
            .build();
        
        limit = limitRepository.save(limit);
        log.info("Limit created successfully: {}", limit.getId());
        
        return convertToDto(limit);
    }
    
    public boolean checkLimit(String userId, BigDecimal amount) {
        log.info("Checking limits for user: {}, amount: {}", userId, amount);
        
        List<Limit> limits = limitRepository.findByUserId(UUID.fromString(userId));
        
        if (limits.isEmpty()) {
            log.warn("No limits found for user: {}", userId);
            return false;
        }
        
        // Check per-transaction limit
        boolean perTransactionOk = checkLimitType(limits, LimitType.PER_TRANSACTION, amount);
        if (!perTransactionOk) {
            log.warn("Per-transaction limit exceeded for user: {}", userId);
            return false;
        }
        
        // Check daily limit
        boolean dailyOk = checkLimitType(limits, LimitType.DAILY, amount);
        if (!dailyOk) {
            log.warn("Daily limit exceeded for user: {}", userId);
            return false;
        }
        
        // Check monthly limit
        boolean monthlyOk = checkLimitType(limits, LimitType.MONTHLY, amount);
        if (!monthlyOk) {
            log.warn("Monthly limit exceeded for user: {}", userId);
            return false;
        }
        
        log.info("All limit checks passed for user: {}", userId);
        return true;
    }
    
    private boolean checkLimitType(List<Limit> limits, LimitType limitType, BigDecimal amount) {
        return limits.stream()
            .filter(l -> l.getLimitType() == limitType)
            .findFirst()
            .map(limit -> {
                // Reset limit if needed
                if (LocalDate.now().isAfter(limit.getResetDate())) {
                    limit.setCurrentUtilization(BigDecimal.ZERO);
                    limit.setResetDate(calculateResetDate(limitType));
                    limitRepository.save(limit);
                }
                
                BigDecimal newUtilization = limit.getCurrentUtilization().add(amount);
                return newUtilization.compareTo(limit.getMaxAmount()) <= 0;
            })
            .orElse(true); // If no limit of this type, allow
    }
    
    @Transactional
    public void updateUtilization(String userId, BigDecimal amount) {
        log.info("Updating utilization for user: {}, amount: {}", userId, amount);
        
        List<Limit> limits = limitRepository.findByUserId(UUID.fromString(userId));
        
        for (Limit limit : limits) {
            // Reset if needed
            if (LocalDate.now().isAfter(limit.getResetDate())) {
                limit.setCurrentUtilization(BigDecimal.ZERO);
                limit.setResetDate(calculateResetDate(limit.getLimitType()));
            }
            
            limit.setCurrentUtilization(limit.getCurrentUtilization().add(amount));
            limitRepository.save(limit);
        }
        
        log.info("Utilization updated successfully for user: {}", userId);
    }
    
    public List<LimitDto> getUserLimits(String userId) {
        log.info("Fetching limits for user: {}", userId);
        return limitRepository.findByUserId(UUID.fromString(userId))
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    private LocalDate calculateResetDate(LimitType limitType) {
        LocalDate now = LocalDate.now();
        return switch (limitType) {
            case PER_TRANSACTION -> now.plusYears(100); // Never resets
            case DAILY -> now.plusDays(1);
            case MONTHLY -> now.plusMonths(1);
        };
    }
    
    private LimitDto convertToDto(Limit limit) {
        return LimitDto.builder()
            .id(limit.getId().toString())
            .userId(limit.getUserId().toString())
            .limitType(limit.getLimitType())
            .maxAmount(limit.getMaxAmount())
            .currency(limit.getCurrency())
            .currentUtilization(limit.getCurrentUtilization())
            .remainingAmount(limit.getMaxAmount().subtract(limit.getCurrentUtilization()))
            .resetDate(limit.getResetDate())
            .build();
    }
}