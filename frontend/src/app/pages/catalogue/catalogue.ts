import { Component, OnInit, inject } from '@angular/core';
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
import { Genre, Mood, TrackDto } from '../../models/track';
import { TrackService } from '../../services/track';
import { ArtistService } from '../../services/artist';
import { PlayerService } from '../../services/player';
import { FavoriteService } from '../../services/favorite';
import { AuthService } from '../../services/auth';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
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
    MatSnackBarModule,
  ],
  templateUrl: './catalogue.html',
  styleUrl: './catalogue.scss'
})
export class Catalogue implements OnInit {
  tracks: TrackDto[] = [];
  filteredTracks: TrackDto[] = [];
  artistMap = new Map<number, string>();
  favoriteIds = new Set<number>();
  isLoading = false;
  isAdmin = false;
  currentUserEmail: string | null = null;

  searchQuery = '';
  selectedGenre: Genre | '' = '';
  selectedMood: Mood | '' = '';

  genres: Genre[] = ['POP', 'ROCK', 'JAZZ', 'CLASSICAL', 'ELECTRONIC', 'HIP_HOP', 'REGGAE', 'METAL', 'RNB', 'OTHER'];
  moods: Mood[] = ['HAPPY', 'SAD', 'CHILL', 'FOCUS', 'HYPE', 'ROMANTIC', 'WORKOUT', 'ANGRY'];

  private trackService = inject(TrackService);
  private artistService = inject(ArtistService);
  private playerService = inject(PlayerService);
  private favoriteService = inject(FavoriteService);
  private authService = inject(AuthService);
  private snackBar = inject(MatSnackBar);

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.currentUserEmail = this.authService.currentUserValue?.email ?? null;
    this.loadTracks();
    // S'abonne au store centralisé — se met à jour quelle que soit la source
    this.favoriteService.favoriteIds$.subscribe(ids => {
      this.favoriteIds = ids;
    });
  }

  loadTracks(): void {
    this.isLoading = true;
    forkJoin({
      artists: this.artistService.getAll(),
      tracks: this.trackService.getAll()
    }).subscribe({
      next: ({ artists, tracks }) => {
        artists.forEach(a => this.artistMap.set(a.id, a.name));
        this.tracks = tracks as unknown as TrackDto[];
        this.filteredTracks = tracks as unknown as TrackDto[];
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; }
    });
  }

  isFavorite(trackId: number): boolean {
    return this.favoriteIds.has(trackId);
  }

  toggleFavorite(track: TrackDto): void {
    if (this.isFavorite(track.id)) {
      this.favoriteService.remove(track.id).subscribe();
    } else {
      this.favoriteService.add(track.id).subscribe();
    }
  }

  getArtistName(artistId: number): string {
    return this.artistMap.get(artistId) || 'Artiste inconnu';
  }

  playTrack(track: TrackDto): void {
    const queue = this.filteredTracks.map(t => ({
      id: t.id,
      title: t.title,
      artist: this.getArtistName(t.artistId)
    }));
    const index = queue.findIndex(t => t.id === track.id);
    this.playerService.setQueue(queue, index >= 0 ? index : 0);
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

  canDelete(track: TrackDto): boolean {
    return this.isAdmin || (track.createdByEmail != null && track.createdByEmail === this.currentUserEmail);
  }

  deleteTrack(track: TrackDto): void {
    if (!confirm(`Supprimer "${track.title}" définitivement ?`)) return;
    this.trackService.delete(track.id).subscribe({
      next: () => {
        this.tracks = this.tracks.filter(t => t.id !== track.id);
        this.filteredTracks = this.filteredTracks.filter(t => t.id !== track.id);
        this.snackBar.open(`"${track.title}" supprimé`, 'OK', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Erreur lors de la suppression', 'OK', { duration: 3000 });
      }
    });
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
