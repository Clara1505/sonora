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
import { Track, Genre, Mood } from '../../models/track';

// Données mockées en attendant le backend
const MOCK_TRACKS: Track[] = [
  {
    id: 1,
    title: 'Get Lucky',
    duration: 248,
    genre: 'POP',
    mood: 'HAPPY',
    artist: { id: 1, name: 'Daft Punk' },
    album: { id: 1, title: 'Random Access Memories', releaseYear: 2013, artist: { id: 1, name: 'Daft Punk' } }
  },
  {
    id: 2,
    title: 'Bohemian Rhapsody',
    duration: 355,
    genre: 'ROCK',
    mood: 'HYPE',
    artist: { id: 2, name: 'Queen' },
    album: { id: 2, title: 'A Night at the Opera', releaseYear: 1975, artist: { id: 2, name: 'Queen' } }
  },
  {
    id: 3,
    title: 'So What',
    duration: 562,
    genre: 'JAZZ',
    mood: 'CHILL',
    artist: { id: 3, name: 'Miles Davis' },
    album: { id: 3, title: 'Kind of Blue', releaseYear: 1959, artist: { id: 3, name: 'Miles Davis' } }
  },
  {
    id: 4,
    title: 'Lose Yourself',
    duration: 326,
    genre: 'HIP_HOP',
    mood: 'HYPE',
    artist: { id: 4, name: 'Eminem' },
    album: { id: 4, title: '8 Mile', releaseYear: 2002, artist: { id: 4, name: 'Eminem' } }
  },
  {
    id: 5,
    title: 'Blinding Lights',
    duration: 200,
    genre: 'POP',
    mood: 'WORKOUT',
    artist: { id: 5, name: 'The Weeknd' },
    album: { id: 5, title: 'After Hours', releaseYear: 2020, artist: { id: 5, name: 'The Weeknd' } }
  },
  {
    id: 6,
    title: 'Clair de Lune',
    duration: 318,
    genre: 'CLASSICAL',
    mood: 'CHILL',
    artist: { id: 6, name: 'Debussy' },
    album: { id: 6, title: 'Suite bergamasque', releaseYear: 1905, artist: { id: 6, name: 'Debussy' } }
  },
];

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
  tracks: Track[] = [];
  filteredTracks: Track[] = [];
  isLoading = false;

  searchQuery = '';
  selectedGenre: Genre | '' = '';
  selectedMood: Mood | '' = '';

  genres: Genre[] = ['POP', 'ROCK', 'JAZZ', 'CLASSICAL', 'ELECTRONIC', 'HIP_HOP', 'REGGAE', 'METAL', 'RNB', 'OTHER'];
  moods: Mood[] = ['HAPPY', 'SAD', 'CHILL', 'FOCUS', 'HYPE', 'ROMANTIC', 'WORKOUT', 'ANGRY'];

  ngOnInit(): void {
    this.loadTracks();
  }

  loadTracks(): void {
    this.isLoading = true;
    // Simuler un appel API
    setTimeout(() => {
      this.tracks = MOCK_TRACKS;
      this.filteredTracks = MOCK_TRACKS;
      this.isLoading = false;
    }, 500);
  }

  applyFilters(): void {
    this.filteredTracks = this.tracks.filter(track => {
      const matchesSearch = !this.searchQuery ||
        track.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        track.artist.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        track.album?.title.toLowerCase().includes(this.searchQuery.toLowerCase());

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