package com.bank.userservice.controller;

import com.bank.userservice.model.dto.*;
import com.bank.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for user: {}", request.getUsername());
        LoginResponse response = userService.authenticate(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request for user: {}", request.getUsername());
        UserDto user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Profile request for user: {}", userDetails.getUsername());
        UserDto user = userService.getUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
        log.info("Get user request for ID: {}", id);
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}