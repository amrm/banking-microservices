package com.bank.userservice.repository;

import com.bank.userservice.model.graph.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGraphRepository extends Neo4jRepository<UserNode, String> {
    
    Optional<UserNode> findByUserId(String userId);
    
    @Query("MATCH (u:User {userId: $userId})-[:OWNS]->(a:Account) RETURN a")
    java.util.List<String> findUserAccounts(String userId);
}