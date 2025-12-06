package com.bank.userservice.service;

import com.bank.userservice.model.entity.User;
import com.bank.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPasswordHash())
            .authorities(Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())))
            .accountExpired(false)
            .accountLocked(user.getStatus() == com.bank.userservice.model.entity.UserStatus.LOCKED)
            .credentialsExpired(false)
            .disabled(user.getStatus() != com.bank.userservice.model.entity.UserStatus.ACTIVE)
            .build();
    }
}