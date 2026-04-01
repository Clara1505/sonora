import { Routes } from '@angular/router';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home').then(m => m.Home),
  },
  {
    path: 'catalogue',
    loadComponent: () => import('./pages/catalogue/catalogue').then(m => m.Catalogue),
    canActivate: [authGuard],
  },
  {
    path: 'tracks/:id',
    loadComponent: () => import('./pages/track-detail/track-detail').then(m => m.TrackDetail),
    canActivate: [authGuard],
  },
  {
    path: 'playlists',
    loadComponent: () => import('./pages/playlist/playlist').then(m => m.PlaylistPage),
    canActivate: [authGuard],
  },
  {
    path: 'profile',
    loadComponent: () => import('./pages/profile/profile').then(m => m.Profile),
    canActivate: [authGuard],
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then(m => m.Login),
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register').then(m => m.Register),
  },
  {
    path: '**',
    redirectTo: '',
  },
];