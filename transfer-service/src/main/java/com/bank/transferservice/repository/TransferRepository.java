package com.bank.transferservice.repository;

import com.bank.transferservice.model.entity.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, UUID> {
    
    Optional<Transfer> findByTransferReference(String transferReference);
    
    Page<Transfer> findByUserId(UUID userId, Pageable pageable);
    
    List<Transfer> findByUserIdAndInitiatedAtBetween(
        UUID userId, 
        LocalDateTime from, 
        LocalDateTime to
    );
}