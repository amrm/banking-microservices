import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Account } from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = environment.services.account;

  constructor(private http: HttpClient) {}

  getUserAccounts(userId: string): Observable<Account[]> {
    return this.http.get<Account[]>(`${this.apiUrl}/accounts/user/${userId}`);
  }

  getAccount(accountId: string): Observable<Account> {
    return this.http.get<Account>(`${this.apiUrl}/accounts/${accountId}`);
  }

  getBalance(accountId: string): Observable<{ balance: number }> {
    return this.http.get<{ balance: number }>(`${this.apiUrl}/accounts/${accountId}/balance`);
  }
}
