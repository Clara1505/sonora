import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatDividerModule } from '@angular/material/divider';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';
import { User } from '../../models/user';
import { PlayHistory } from '../../models/play-history';

const MOCK_HISTORY: PlayHistory[] = [
  {
    id: 1,
    track: { id: 1, title: 'Get Lucky', duration: 248, genre: 'POP', mood: 'HAPPY', artist: { id: 1, name: 'Daft Punk' } },
    playedAt: '2024-03-27T10:30:00',
    durationListened: 248
  },
  {
    id: 2,
    track: { id: 2, title: 'Bohemian Rhapsody', duration: 355, genre: 'ROCK', mood: 'HYPE', artist: { id: 2, name: 'Queen' } },
    playedAt: '2024-03-27T11:00:00',
    durationListened: 355
  },
  {
    id: 3,
    track: { id: 3, title: 'So What', duration: 562, genre: 'JAZZ', mood: 'CHILL', artist: { id: 3, name: 'Miles Davis' } },
    playedAt: '2024-03-26T20:00:00',
    durationListened: 300
  },
];

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatDividerModule,
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class Profile implements OnInit {
  currentUser: User | null = null;
  history: PlayHistory[] = [];

  stats = {
    totalTracks: 12,
    totalPlaylists: 3,
    totalListeningTime: '4h 32min',
    favoriteGenre: 'JAZZ',
  };

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (!user) this.router.navigate(['/login']);
    });
    this.history = MOCK_HISTORY;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('fr-FR', {
      day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
    });
  }

  formatDuration(seconds: number): string {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }
}