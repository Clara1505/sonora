import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { authGuard } from './auth-guard';
import { AuthService } from '../services/auth';

describe('authGuard', () => {
  let authService: Partial<AuthService>;
  let router: Partial<Router>;

  const runGuard = () =>
    TestBed.runInInjectionContext(() =>
      authGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    );

  beforeEach(() => {
    authService = { isLoggedIn: vi.fn() };
    router = { navigate: vi.fn() };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
      ],
    });
  });

  it('should allow navigation when the user is logged in', () => {
    (authService.isLoggedIn as ReturnType<typeof vi.fn>).mockReturnValue(true);
    expect(runGuard()).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should redirect to /login when the user is not logged in', () => {
    (authService.isLoggedIn as ReturnType<typeof vi.fn>).mockReturnValue(false);
    expect(runGuard()).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});