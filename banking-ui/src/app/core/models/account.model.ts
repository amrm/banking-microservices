export interface Account {
  id: string;
  userId: string;
  accountNumber: string;
  accountType: 'CHECKING' | 'SAVINGS' | 'BUSINESS';
  balance: number;
  currency: string;
  status: string;
  createdAt: Date;
}
