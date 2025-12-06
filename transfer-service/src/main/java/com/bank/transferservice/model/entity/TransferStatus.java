package com.bank.transferservice.model.entity;

public enum TransferStatus {
    INITIATED,
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
    COMPLETED,
    FAILED
}