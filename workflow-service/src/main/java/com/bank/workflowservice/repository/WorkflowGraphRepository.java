package com.bank.workflowservice.repository;

import com.bank.workflowservice.model.graph.WorkflowNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowGraphRepository extends Neo4jRepository<WorkflowNode, String> {
    
    @Query("MATCH (t:Transfer {transferId: $transferId}) " +
           "MATCH (w:Workflow {workflowId: $workflowId}) " +
           "MERGE (t)-[:REQUIRES_WORKFLOW]->(w)")
    void linkWorkflowToTransfer(String transferId, String workflowId);
    
    @Query("MATCH (u:User {userId: $userId}) " +
           "MATCH (w:Workflow {workflowId: $workflowId}) " +
           "MERGE (u)-[r:APPROVED]->(w) " +
           "SET r.timestamp = datetime()")
    void linkApproverToWorkflow(String userId, String workflowId);
}