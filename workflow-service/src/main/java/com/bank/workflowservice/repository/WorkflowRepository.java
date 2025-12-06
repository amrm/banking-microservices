package com.bank.workflowservice.repository;

import com.bank.workflowservice.model.entity.Workflow;
import com.bank.workflowservice.model.entity.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {
    
    Optional<Workflow> findByTransferId(UUID transferId);
    
    List<Workflow> findByStatus(WorkflowStatus status);
    
    List<Workflow> findByAssignedTo(String assignedTo);
}