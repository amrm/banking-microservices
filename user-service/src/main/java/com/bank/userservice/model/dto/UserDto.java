package com.bank.userservice.model.dto;

import com.bank.userservice.model.entity.UserStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private UserStatus status;
    private String role;
    private LocalDateTime createdAt;
    
}