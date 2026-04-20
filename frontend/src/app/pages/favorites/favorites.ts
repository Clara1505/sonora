import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FavoriteService, FavoriteResponse } from '../../services/favorite';
import { PlayerService } from '../../services/player';

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './favorites.html',
  styleUrl: './favorites.scss'
})
export class Favorites implements OnInit {
  favorites: FavoriteResponse[] = [];
  isLoading = false;

  constructor(
    private favoriteService: FavoriteService,
    private playerService: PlayerService
  ) {}

  ngOnInit(): void {
    this.loadFavorites();
  }

  loadFavorites(): void {
    this.isLoading = true;
    this.favoriteService.getAll().subscribe({
      next: (favorites) => {
        this.favorites = favorites;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  playTrack(favorite: FavoriteResponse): void {
    this.playerService.play(favorite.trackId, favorite.trackTitle, favorite.artistName);
  }

  removeFavorite(favorite: FavoriteResponse): void {
    this.favoriteService.remove(favorite.trackId).subscribe(() => {
      this.favorites = this.favorites.filter(f => f.id !== favorite.id);
    });
  }

  formatDuration(): string {
    return '';
  }

  getGenreColor(genre: string): string {
    const colors: Record<string, string> = {
      POP: '#e91e63', ROCK: '#f44336', JAZZ: '#9c27b0',
      CLASSICAL: '#3f51b5', ELECTRONIC: '#00bcd4', HIP_HOP: '#ff9800',
      REGGAE: '#4caf50', METAL: '#607d8b', RNB: '#ff5722', OTHER: '#9e9e9e'
    };
    return colors[genre] || '#9e9e9e';
  }
}