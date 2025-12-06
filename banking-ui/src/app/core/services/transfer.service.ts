import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Transfer, TransferRequest, TransferResponse } from '../models/transfer.model';

@Injectable({
  providedIn: 'root'
})
export class TransferService {
  private apiUrl = environment.services.transfer;

  constructor(private http: HttpClient) {}

  initiateTransfer(request: TransferRequest): Observable<TransferResponse> {
    return this.http.post<TransferResponse>(`${this.apiUrl}/transfers/initiate`, request);
  }

  getTransfer(transferId: string): Observable<Transfer> {
    return this.http.get<Transfer>(`${this.apiUrl}/transfers/${transferId}`);
  }

  getUserTransfers(userId: string, page: number = 0, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(`${this.apiUrl}/transfers/user/${userId}`, { params });
  }

  getTransferHistory(userId: string, from: Date, to: Date): Observable<Transfer[]> {
    const params = new HttpParams()
      .set('from', from.toISOString())
      .set('to', to.toISOString());

    return this.http.get<Transfer[]>(`${this.apiUrl}/transfers/user/${userId}/history`, { params });
  }
}
