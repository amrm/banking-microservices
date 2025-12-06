package com.bank.transferservice.repository;

import com.bank.transferservice.model.graph.TransferNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferGraphRepository extends Neo4jRepository<TransferNode, String> {
    
    @Query("MATCH (t:Transfer {transferId: $transferId}) " +
           "MATCH (from:Account {accountId: $fromAccountId}) " +
           "MATCH (to:Account {accountId: $toAccountId}) " +
           "MERGE (t)-[:FROM_ACCOUNT]->(from) " +
           "MERGE (t)-[:TO_ACCOUNT]->(to) " +
           "MERGE (from)-[r:TRANSACTED_WITH]->(to) " +
           "ON CREATE SET r.count = 1, r.totalAmount = $amount " +
           "ON MATCH SET r.count = r.count + 1, r.totalAmount = r.totalAmount + $amount")
    void linkTransferToAccounts(String transferId, String fromAccountId, 
                                String toAccountId, Double amount);
    
    @Query("MATCH (u:User {userId: $userId})-[:INITIATED]->(t:Transfer) " +
           "MERGE (u)-[:INITIATED]->(t)")
    void linkTransferToUser(String userId, String transferId);
}