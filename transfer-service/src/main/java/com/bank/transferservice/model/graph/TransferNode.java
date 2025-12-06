package com.bank.transferservice.model.graph;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Node("Transfer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferNode {
    
    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;
    
    @Property("transferId")
    private String transferId;
    
    @Property("amount")
    private BigDecimal amount;
    
    @Property("currency")
    private String currency;
    
    @Property("status")
    private String status;
    
    @Property("timestamp")
    private LocalDateTime timestamp;
    
    @Property("description")
    private String description;
}