package com.bank.userservice.service;

import com.bank.userservice.model.dto.*;
import com.bank.userservice.model.entity.User;
import com.bank.userservice.model.entity.UserStatus;
import com.bank.userservice.model.graph.UserNode;
import com.bank.userservice.repository.UserGraphRepository;
import com.bank.userservice.repository.UserRepository;
import com.bank.userservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserGraphRepository userGraphRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    
    @Transactional
    public LoginResponse authenticate(LoginRequest request) {
        log.info("Authenticating user: {}", request.getUsername());
        
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        
        // Update session
        String sessionId = UUID.randomUUID().toString();
        user.setSessionId(sessionId);
        userRepository.save(user);
        
        log.info("User {} authenticated successfully. SessionId: {}", user.getUsername(), sessionId);
        
        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(86400L)
            .user(convertToDto(user))
            .build();
    }
    
    @Transactional
    public UserDto register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .status(UserStatus.ACTIVE)
            .role("USER")
            .build();
        
        user = userRepository.save(user);
        
        // Create user node in Neo4j
        UserNode userNode = UserNode.builder()
            .userId(user.getId().toString())
            .username(user.getUsername())
            .email(user.getEmail())
            .createdAt(LocalDateTime.now())
            .build();
        
        userGraphRepository.save(userNode);
        
        log.info("User {} registered successfully with ID: {}", user.getUsername(), user.getId());
        
        return convertToDto(user);
    }
    
    public UserDto getUserById(String id) {
        User user = userRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }
    
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }
    
    private UserDto convertToDto(User user) {
        return UserDto.builder()
            .id(user.getId().toString())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .status(user.getStatus())
            .role(user.getRole())
            .createdAt(user.getCreatedAt())
            .build();
    }
}