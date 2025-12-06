package com.bank.corebankingservice.repository;

import com.bank.corebankingservice.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    
    List<Transaction> findByTransferId(UUID transferId);
    
    List<Transaction> findByAccountId(UUID accountId);
}