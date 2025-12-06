package com.bank.accountservice.model.graph;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.math.BigDecimal;

@Node("Account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountNode {
    
    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;
    
    @Property("accountId")
    private String accountId;
    
    @Property("accountNumber")
    private String accountNumber;
    
    @Property("accountType")
    private String accountType;
    
    @Property("currentBalance")
    private BigDecimal currentBalance;
}