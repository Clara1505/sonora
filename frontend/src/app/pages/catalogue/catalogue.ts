import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Genre, Mood } from '../../models/track';
import { TrackService } from '../../services/track';
import { ArtistService } from '../../services/artist';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-catalogue',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatChipsModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './catalogue.html',
  styleUrl: './catalogue.scss'
})
export class Catalogue implements OnInit {
  tracks: any[] = [];
  filteredTracks: any[] = [];
  artistMap: Map<number, string> = new Map();
  isLoading = false;

  searchQuery = '';
  selectedGenre: Genre | '' = '';
  selectedMood: Mood | '' = '';

  genres: Genre[] = ['POP', 'ROCK', 'JAZZ', 'CLASSICAL', 'ELECTRONIC', 'HIP_HOP', 'REGGAE', 'METAL', 'RNB', 'OTHER'];
  moods: Mood[] = ['HAPPY', 'SAD', 'CHILL', 'FOCUS', 'HYPE', 'ROMANTIC', 'WORKOUT', 'ANGRY'];

  constructor(
    private trackService: TrackService,
    private artistService: ArtistService
  ) {}

  ngOnInit(): void {
    this.loadTracks();
  }

  loadTracks(): void {
    this.isLoading = true;
    forkJoin({
      artists: this.artistService.getAll(),
      tracks: this.trackService.getAll()
    }).subscribe({
      next: ({ artists, tracks }) => {
        artists.forEach(a => this.artistMap.set(a.id, a.name));
        this.tracks = tracks;
        this.filteredTracks = tracks;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  getArtistName(artistId: number): string {
    return this.artistMap.get(artistId) || 'Artiste inconnu';
  }

  applyFilters(): void {
    this.filteredTracks = this.tracks.filter(track => {
      const artistName = this.artistMap.get(track.artistId) || '';
      const matchesSearch = !this.searchQuery ||
        track.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        artistName.toLowerCase().includes(this.searchQuery.toLowerCase());

      const matchesGenre = !this.selectedGenre || track.genre === this.selectedGenre;
      const matchesMood = !this.selectedMood || track.mood === this.selectedMood;

      return matchesSearch && matchesGenre && matchesMood;
    });
  }

  resetFilters(): void {
    this.searchQuery = '';
    this.selectedGenre = '';
    this.selectedMood = '';
    this.filteredTracks = this.tracks;
  }

  formatDuration(seconds: number): string {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  getGenreColor(genre: Genre): string {
    const colors: Record<Genre, string> = {
      POP: '#e91e63', ROCK: '#f44336', JAZZ: '#9c27b0',
      CLASSICAL: '#3f51b5', ELECTRONIC: '#00bcd4', HIP_HOP: '#ff9800',
      REGGAE: '#4caf50', METAL: '#607d8b', RNB: '#ff5722', OTHER: '#9e9e9e'
    };
    return colors[genre] || '#9e9e9e';
  }
}