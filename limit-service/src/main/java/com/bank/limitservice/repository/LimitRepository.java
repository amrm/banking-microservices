package com.bank.limitservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.limitservice.model.entity.Limit;
import com.bank.limitservice.model.entity.LimitType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LimitRepository extends JpaRepository<Limit, UUID> {
    
    List<Limit> findByUserId(UUID userId);
    
    Optional<Limit> findByUserIdAndLimitType(UUID userId, LimitType limitType);
}