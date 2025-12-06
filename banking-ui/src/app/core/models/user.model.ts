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
