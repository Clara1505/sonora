export type Role = 'USER' | 'ADMIN';

export interface User {
  id: number;
  email: string;
  role: Role;
  createdAt: string;
}

export interface AuthRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  role: Role;
}

export interface RegisterRequest {
  email: string;
  password: string;
}