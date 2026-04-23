import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';
import { StatsService, UserStatsResponse } from '../../services/stats';
import { AdminService, AdminStats } from '../../services/admin';
import { PlayerService } from '../../services/player';
import { User } from '../../models/user';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatDividerModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class Profile implements OnInit {
  currentUser: User | null = null;
  userStats: UserStatsResponse | null = null;
  adminStats: AdminStats | null = null;
  isLoading = false;

  private authService = inject(AuthService);
  private statsService = inject(StatsService);
  private adminService = inject(AdminService);
  private playerService = inject(PlayerService);
  private router = inject(Router);

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (!user) {
        this.router.navigate(['/login']);
      } else {
        this.loadStats();
      }
    });
  }

  loadStats(): void {
    this.isLoading = true;
    this.statsService.getMyStats().subscribe({
      next: (stats) => {
        this.userStats = stats;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
    if (this.authService.isAdmin()) {
      this.adminService.getStats().subscribe({
        next: (stats) => { this.adminStats = stats; },
        error: () => { console.error('Failed to load admin stats'); }
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  playTrack(trackId: number, trackTitle: string): void {
    this.playerService.play(trackId, trackTitle, '');
  }

  formatTotalDuration(seconds: number): string {
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    if (h > 0) return `${h}h ${m}min`;
    return `${m}min`;
  }

  getTotalListeningTime(): string {
    if (!this.userStats?.topTracks) return '0min';
    const total = this.userStats.topTracks.reduce(
      (acc, t) => acc + (t.totalDurationListened || 0), 0
    );
    return this.formatTotalDuration(total);
  }
}
