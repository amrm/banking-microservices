package com.bank.userservice.model.graph;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDateTime;

@Node("User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNode {
    
    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;
    
    @Property("userId")
    private String userId;
    
    @Property("username")
    private String username;
    
    @Property("email")
    private String email;
    
    @Property("createdAt")
    private LocalDateTime createdAt;
    
}