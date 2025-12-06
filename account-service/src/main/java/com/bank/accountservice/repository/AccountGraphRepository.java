package com.bank.accountservice.repository;

import com.bank.accountservice.model.graph.AccountNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountGraphRepository extends Neo4jRepository<AccountNode, String> {
    
    Optional<AccountNode> findByAccountId(String accountId);
    
    @Query("MATCH (u:User {userId: $userId})-[:OWNS]->(a:Account) RETURN a")
    java.util.List<AccountNode> findAccountsByUserId(String userId);
    
    @Query("MATCH (u:User {userId: $userId})-[:OWNS]->(a:Account {accountId: $accountId}) " +
           "MERGE (u)-[:OWNS]->(a)")
    void linkAccountToUser(String userId, String accountId);
}