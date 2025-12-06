package com.bank.workflowservice.model.graph;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDateTime;

@Node("Workflow")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowNode {
    
    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;
    
    @Property("workflowId")
    private String workflowId;
    
    @Property("status")
    private String status;
    
    @Property("assignedTo")
    private String assignedTo;
    
    @Property("createdAt")
    private LocalDateTime createdAt;
}