export interface Transfer {
  id: string;
  transferReference: string;
  userId: string;
  fromAccountId: string;
  toAccountId: string;
  amount: number;
  currency: string;
  status: TransferStatus;
  description?: string;
  workflowRequired: boolean;
  workflowId?: string;
  initiatedAt: Date;
  completedAt?: Date;
}

export enum TransferStatus {
  INITIATED = 'INITIATED',
  PENDING_APPROVAL = 'PENDING_APPROVAL',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED'
}

export interface TransferRequest {
  fromAccountId: string;
  toAccountId: string;
  amount: number;
  currency: string;
  description?: string;
}

export interface TransferResponse {
  transferId: string;
  transferReference: string;
  fromAccountId: string;
  toAccountId: string;
  amount: number;
  currency: string;
  status: TransferStatus;
  description?: string;
  workflowRequired: boolean;
  initiatedAt: Date;
  message: string;
}
