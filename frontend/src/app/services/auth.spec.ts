import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth';
import { AuthResponse } from '../models/user';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const mockAuthResponse: AuthResponse = {
    token: 'mock-jwt-token',
    email: 'test@sonora.dev',
    role: 'USER',
  };

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('login()', () => {
    it('should POST to /api/auth/login and store the token', () => {
      service.login({ email: 'test@sonora.dev', password: 'secret' }).subscribe(res => {
        expect(res.token).toBe('mock-jwt-token');
      });

      const req = httpMock.expectOne(r => r.url.includes('/api/auth/login'));
      expect(req.request.method).toBe('POST');
      req.flush(mockAuthResponse);

      expect(localStorage.getItem('sonora_token')).toBe('mock-jwt-token');
    });

    it('should update currentUser$ after login', () => {
      let emittedUser: any;
      service.currentUser$.subscribe(u => (emittedUser = u));

      service.login({ email: 'test@sonora.dev', password: 'secret' }).subscribe();
      httpMock.expectOne(r => r.url.includes('/api/auth/login')).flush(mockAuthResponse);

      expect(emittedUser?.email).toBe('test@sonora.dev');
      expect(emittedUser?.role).toBe('USER');
    });
  });

  describe('register()', () => {
    it('should POST to /api/auth/register and store the session', () => {
      // RegisterRequest n'a que email + password (pas de username)
      service.register({ email: 'new@sonora.dev', password: 'secret' }).subscribe();

      const req = httpMock.expectOne(r => r.url.includes('/api/auth/register'));
      expect(req.request.method).toBe('POST');
      req.flush(mockAuthResponse);

      expect(service.isLoggedIn()).toBe(true);
    });
  });

  describe('logout()', () => {
    it('should clear the token and emit null user', () => {
      localStorage.setItem('sonora_token', 'some-token');
      let emittedUser: any = 'not-null';
      service.currentUser$.subscribe(u => (emittedUser = u));

      service.logout();

      expect(localStorage.getItem('sonora_token')).toBeNull();
      expect(emittedUser).toBeNull();
    });
  });

  describe('isLoggedIn()', () => {
    it('should return false when no token in localStorage', () => {
      expect(service.isLoggedIn()).toBe(false);
    });

    it('should return true when a token exists', () => {
      localStorage.setItem('sonora_token', 'some-token');
      expect(service.isLoggedIn()).toBe(true);
    });
  });

  describe('isAdmin()', () => {
    it('should return false for a USER role', () => {
      service.login({ email: 'test@sonora.dev', password: 'secret' }).subscribe();
      httpMock.expectOne(r => r.url.includes('/api/auth/login')).flush(mockAuthResponse);
      expect(service.isAdmin()).toBe(false);
    });

    it('should return true for an ADMIN role', () => {
      const adminResponse: AuthResponse = { ...mockAuthResponse, role: 'ADMIN' };
      service.login({ email: 'admin@sonora.dev', password: 'secret' }).subscribe();
      httpMock.expectOne(r => r.url.includes('/api/auth/login')).flush(adminResponse);
      expect(service.isAdmin()).toBe(true);
    });
  });

  describe('getToken()', () => {
    it('should return null when not logged in', () => {
      expect(service.getToken()).toBeNull();
    });

    it('should return the stored token after login', () => {
      service.login({ email: 'test@sonora.dev', password: 'secret' }).subscribe();
      httpMock.expectOne(r => r.url.includes('/api/auth/login')).flush(mockAuthResponse);
      expect(service.getToken()).toBe('mock-jwt-token');
    });
  });
});