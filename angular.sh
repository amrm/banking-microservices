#!/bin/bash

# Banking Web App - Complete Setup Script
# This script creates the entire Angular 18 project structure

echo "🚀 Setting up Banking Web App..."

# Create Angular project
echo "📦 Creating Angular project..."
ng new banking-web-app --routing --style=scss --skip-git

cd banking-web-app

# Install dependencies
echo "📥 Installing dependencies..."
npm install @angular/material @angular/cdk @angular/animations
npm install jwt-decode
npm install uuid
npm install @types/uuid --save-dev

# Create directory structure
echo "📁 Creating directory structure..."
mkdir -p src/app/core/{guards,interceptors,services,models}
mkdir -p src/app/features/{auth/login,auth/register,dashboard,accounts,transfers,history}
mkdir -p src/app/shared/components

# Create environment files
echo "🌍 Creating environment files..."
cat > src/environments/environment.ts << 'EOF'
export const environment = {
  production: false,
  apiUrl: 'http://localhost',
  services: {
    user: 'http://localhost:8081/api',
    account: 'http://localhost:8082/api',
    transfer: 'http://localhost:8083/api',
    limit: 'http://localhost:8084/api',
    workflow: 'http://localhost:8085/api'
  }
};
EOF

cat > src/environments/environment.prod.ts << 'EOF'
export const environment = {
  production: true,
  apiUrl: 'http://localhost',
  services: {
    user: 'http://localhost:8081/api',
    account: 'http://localhost:8082/api',
    transfer: 'http://localhost:8083/api',
    limit: 'http://localhost:8084/api',
    workflow: 'http://localhost:8085/api'
  }
};
EOF

# Create Models
echo "📝 Creating models..."

cat > src/app/core/models/user.model.ts << 'EOF'
export interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  status: string;
  role: string;
  createdAt: Date;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}
EOF

cat > src/app/core/models/account.model.ts << 'EOF'
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
EOF

cat > src/app/core/models/transfer.model.ts << 'EOF'
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
EOF

# Create Services
echo "🔧 Creating services..."

cat > src/app/core/services/auth.service.ts << 'EOF'
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse, RegisterRequest, User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private apiUrl = environment.services.user;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadUserFromStorage();
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(\`\${this.apiUrl}/users/login\`, credentials)
      .pipe(
        tap(response => {
          this.storeTokens(response);
          this.currentUserSubject.next(response.user);
        })
      );
  }

  register(request: RegisterRequest): Observable<User> {
    return this.http.post<User>(\`\${this.apiUrl}/users/register\`, request);
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  isAuthenticated(): boolean {
    const token = this.getAccessToken();
    if (!token) return false;

    try {
      const decoded: any = jwtDecode(token);
      return decoded.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  private storeTokens(response: LoginResponse): void {
    localStorage.setItem('accessToken', response.accessToken);
    localStorage.setItem('refreshToken', response.refreshToken);
    localStorage.setItem('user', JSON.stringify(response.user));
  }

  private loadUserFromStorage(): void {
    const userJson = localStorage.getItem('user');
    if (userJson) {
      try {
        const user = JSON.parse(userJson);
        if (this.isAuthenticated()) {
          this.currentUserSubject.next(user);
        } else {
          this.logout();
        }
      } catch {
        this.logout();
      }
    }
  }
}
EOF

cat > src/app/core/services/account.service.ts << 'EOF'
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
    return this.http.get<Account[]>(\`\${this.apiUrl}/accounts/user/\${userId}\`);
  }

  getAccount(accountId: string): Observable<Account> {
    return this.http.get<Account>(\`\${this.apiUrl}/accounts/\${accountId}\`);
  }

  getBalance(accountId: string): Observable<{ balance: number }> {
    return this.http.get<{ balance: number }>(\`\${this.apiUrl}/accounts/\${accountId}/balance\`);
  }
}
EOF

cat > src/app/core/services/transfer.service.ts << 'EOF'
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
    return this.http.post<TransferResponse>(\`\${this.apiUrl}/transfers/initiate\`, request);
  }

  getTransfer(transferId: string): Observable<Transfer> {
    return this.http.get<Transfer>(\`\${this.apiUrl}/transfers/\${transferId}\`);
  }

  getUserTransfers(userId: string, page: number = 0, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(\`\${this.apiUrl}/transfers/user/\${userId}\`, { params });
  }

  getTransferHistory(userId: string, from: Date, to: Date): Observable<Transfer[]> {
    const params = new HttpParams()
      .set('from', from.toISOString())
      .set('to', to.toISOString());

    return this.http.get<Transfer[]>(\`\${this.apiUrl}/transfers/user/\${userId}/history\`, { params });
  }
}
EOF

# Create Interceptors
echo "🔐 Creating interceptors..."

cat > src/app/core/interceptors/auth.interceptor.ts << 'EOF'
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getAccessToken();

  if (token && !req.url.includes('/login') && !req.url.includes('/register')) {
    req = req.clone({
      setHeaders: {
        Authorization: \`Bearer \${token}\`
      }
    });
  }

  return next(req);
};
EOF

cat > src/app/core/interceptors/correlation-id.interceptor.ts << 'EOF'
import { HttpInterceptorFn } from '@angular/common/http';
import { v4 as uuidv4 } from 'uuid';

export const correlationIdInterceptor: HttpInterceptorFn = (req, next) => {
  const correlationId = uuidv4();
  
  req = req.clone({
    setHeaders: {
      'X-Correlation-Id': correlationId
    }
  });

  return next(req);
};
EOF

cat > src/app/core/interceptors/logging.interceptor.ts << 'EOF'
import { HttpInterceptorFn } from '@angular/common/http';
import { tap, catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const loggingInterceptor: HttpInterceptorFn = (req, next) => {
  const started = Date.now();
  
  console.log(\`[HTTP] \${req.method} \${req.url}\`);

  return next(req).pipe(
    tap(() => {
      const elapsed = Date.now() - started;
      console.log(\`[HTTP] \${req.method} \${req.url} - \${elapsed}ms\`);
    }),
    catchError(error => {
      const elapsed = Date.now() - started;
      console.error(\`[HTTP] \${req.method} \${req.url} - Error after \${elapsed}ms\`, error);
      return throwError(() => error);
    })
  );
};
EOF

# Create Guards
echo "🛡️ Creating guards..."

cat > src/app/core/guards/auth.guard.ts << 'EOF'
import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};
EOF

echo "✅ Angular project structure created successfully!"
echo ""
echo "📋 Next steps:"
echo "1. cd banking-web-app"
echo "2. Copy the component files from the documentation"
echo "3. Update app.config.ts and app.routes.ts"
echo "4. Update app.component.ts"
echo "5. Run: ng serve"
echo ""
echo "🎉 Happy coding!"
