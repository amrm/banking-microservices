package com.bank.limitservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.limitservice.model.dto.CreateLimitRequest;
import com.bank.limitservice.model.dto.LimitCheckRequest;
import com.bank.limitservice.model.dto.LimitDto;
import com.bank.limitservice.service.LimitService;

import java.util.List;

@RestController
@RequestMapping("/api/limits")
@RequiredArgsConstructor
@Slf4j
public class LimitController {
    
    private final LimitService limitService;
    
    @PostMapping
    public ResponseEntity<LimitDto> createLimit(@Valid @RequestBody CreateLimitRequest request) {
        log.info("Create limit request for user: {}", request.getUserId());
        LimitDto limit = limitService.createLimit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(limit);
    }
    
    @PostMapping("/check")
    public ResponseEntity<Boolean> checkLimit(@Valid @RequestBody LimitCheckRequest request) {
        log.info("Limit check request for user: {}", request.getUserId());
        boolean withinLimit = limitService.checkLimit(request.getUserId(), request.getAmount());
        return ResponseEntity.ok(withinLimit);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LimitDto>> getUserLimits(@PathVariable String userId) {
        log.info("Get limits for user: {}", userId);
        List<LimitDto> limits = limitService.getUserLimits(userId);
        return ResponseEntity.ok(limits);
    }
}