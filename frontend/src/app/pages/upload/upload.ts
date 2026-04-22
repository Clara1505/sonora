import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatListModule } from '@angular/material/list';
import { HttpClient } from '@angular/common/http';
import { ArtistService } from '../../services/artist';
import { AlbumService, AlbumDto } from '../../services/album';
import { Artist } from '../../models/artist';
import { SpotifyService, SpotifyTrack } from '../../services/spotify';
import { Genre, Mood } from '../../models/track';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatProgressBarModule,
    MatDividerModule,
    MatSnackBarModule,
    MatButtonToggleModule,
    MatListModule,
  ],
  templateUrl: './upload.html',
  styleUrl: './upload.scss'
})
export class Upload implements OnInit {
  private readonly API_URL = `${environment.apiUrl}/api`;

  mode: 'manual' | 'spotify' = 'manual';

  // Spotify
  spotifyQuery = '';
  spotifyResults: SpotifyTrack[] = [];
  selectedSpotifyTrack: SpotifyTrack | null = null;
  spotifyFallbackFile: File | null = null;
  isSearching = false;

  // Fichier audio
  selectedFile: File | null = null;

  // Artiste
  artists: Artist[] = [];
  selectedArtistId: number | null = null;
  showNewArtistForm = false;
  newArtistName = '';
  newArtistBio = '';

  // Album
  albums: AlbumDto[] = [];
  filteredAlbums: AlbumDto[] = [];
  selectedAlbumId: number | null = null;
  showNewAlbumForm = false;
  newAlbumTitle = '';
  newAlbumYear: number = new Date().getFullYear();

  // Track
  trackTitle = '';
  selectedGenre: Genre | '' = '';
  selectedMood: Mood | '' = '';

  genres: Genre[] = ['POP', 'ROCK', 'JAZZ', 'CLASSICAL', 'ELECTRONIC', 'HIP_HOP', 'REGGAE', 'METAL', 'RNB', 'OTHER'];
  moods: Mood[] = ['HAPPY', 'SAD', 'CHILL', 'FOCUS', 'HYPE', 'ROMANTIC', 'WORKOUT', 'ANGRY'];

  isUploading = false;

  private artistService = inject(ArtistService);
  private albumService = inject(AlbumService);
  private spotifyService = inject(SpotifyService);
  private http = inject(HttpClient);
  private snackBar = inject(MatSnackBar);
  private router = inject(Router);

  ngOnInit(): void {
    this.loadArtists();
    this.loadAlbums();
  }

  loadArtists(): void {
    this.artistService.getAll().subscribe(artists => { this.artists = artists; });
  }

  loadAlbums(): void {
    this.albumService.getAll().subscribe(albums => {
      this.albums = albums;
      this.filterAlbums();
    });
  }

  onArtistChange(): void {
    this.selectedAlbumId = null;
    this.filterAlbums();
  }

  filterAlbums(): void {
    if (this.selectedArtistId) {
      this.filteredAlbums = this.albums.filter(a => a.artistId === this.selectedArtistId);
    } else {
      this.filteredAlbums = this.albums;
    }
  }

  onFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.selectedFile = file;
      if (!this.trackTitle) this.trackTitle = file.name.replace(/\.[^.]+$/, '');
    }
  }

  // ── Spotify ──────────────────────────────────

  searchSpotify(): void {
    if (!this.spotifyQuery.trim()) return;
    this.isSearching = true;
    this.spotifyResults = [];
    this.spotifyService.searchTracks(this.spotifyQuery).subscribe({
      next: (tracks) => { this.spotifyResults = tracks; this.isSearching = false; },
      error: () => {
        this.isSearching = false;
        this.snackBar.open('Erreur lors de la recherche Spotify', 'OK', { duration: 3000 });
      }
    });
  }

  selectSpotifyTrack(track: SpotifyTrack): void {
    this.selectedSpotifyTrack = track;
    this.spotifyFallbackFile = null; // reset le fichier fallback à chaque nouvelle sélection
    this.trackTitle = track.name;
    this.newArtistName = track.artists[0]?.name || '';
    this.newAlbumTitle = track.album.name;
    this.newAlbumYear = parseInt(track.album.release_date.substring(0, 4));
    this.showNewArtistForm = true;
    this.showNewAlbumForm = true;
  }

  onSpotifyFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) this.spotifyFallbackFile = file;
  }

  formatDuration(ms: number): string {
    const m = Math.floor(ms / 60000);
    const s = Math.floor((ms % 60000) / 1000);
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  // ── Upload ───────────────────────────────────

  async createArtistIfNeeded(): Promise<number> {
    if (!this.showNewArtistForm) return this.selectedArtistId!;
    const artist = await this.artistService.create({ name: this.newArtistName, bio: this.newArtistBio }).toPromise();
    this.artists = [...this.artists, artist!];
    return artist!.id;
  }

  async createAlbumIfNeeded(artistId: number): Promise<number> {
    if (!this.showNewAlbumForm) return this.selectedAlbumId!;
    const album = await this.albumService.create({ title: this.newAlbumTitle, releaseYear: this.newAlbumYear, artistId }).toPromise();
    this.albums = [...this.albums, album!];
    return album!.id;
  }

  async fetchPreviewAsFile(previewUrl: string): Promise<File> {
    const response = await fetch(previewUrl);
    const blob = await response.blob();
    return new File([blob], `${this.trackTitle}.mp3`, { type: 'audio/mpeg' });
  }

  async upload(): Promise<void> {
    if (this.mode === 'spotify') {
      await this.uploadFromSpotify();
    } else {
      await this.uploadManual();
    }
  }

  async uploadFromSpotify(): Promise<void> {
    const hasPreview = !!this.selectedSpotifyTrack?.preview_url;
    const hasFile = !!this.spotifyFallbackFile;

    if (!hasPreview && !hasFile) {
      this.snackBar.open('Veuillez fournir un fichier audio', 'OK', { duration: 3000 });
      return;
    }

    this.isUploading = true;
    try {
      const artistId = await this.createArtistIfNeeded();
      const albumId = await this.createAlbumIfNeeded(artistId);

      const file = hasPreview
        ? await this.fetchPreviewAsFile(this.selectedSpotifyTrack!.preview_url!)
        : this.spotifyFallbackFile!;

      const metadata = {
        title: this.trackTitle,
        genre: this.selectedGenre || null,
        mood: this.selectedMood || null,
        artistId,
        albumId: albumId || null
      };

      const formData = new FormData();
      formData.append('file', file);
      formData.append('metadata', new Blob([JSON.stringify(metadata)], { type: 'application/json' }));

      this.http.post(`${this.API_URL}/tracks/upload`, formData).subscribe({
        next: () => {
          this.isUploading = false;
          this.snackBar.open('Titre importé depuis Spotify !', 'OK', { duration: 3000 });
          this.router.navigate(['/catalogue']);
        },
        error: () => {
          this.isUploading = false;
          this.snackBar.open('Erreur lors de l\'upload', 'OK', { duration: 3000 });
        }
      });
    } catch {
      this.isUploading = false;
      this.snackBar.open('Erreur lors de l\'import', 'OK', { duration: 3000 });
    }
  }

  async uploadManual(): Promise<void> {
    if (!this.selectedFile || !this.trackTitle) {
      this.snackBar.open('Veuillez sélectionner un fichier et un titre', 'OK', { duration: 3000 });
      return;
    }
    if (!this.showNewArtistForm && !this.selectedArtistId) {
      this.snackBar.open('Veuillez sélectionner ou créer un artiste', 'OK', { duration: 3000 });
      return;
    }
    this.isUploading = true;
    try {
      const artistId = await this.createArtistIfNeeded();
      const albumId = await this.createAlbumIfNeeded(artistId);
      const metadata = {
        title: this.trackTitle,
        genre: this.selectedGenre || null,
        mood: this.selectedMood || null,
        artistId,
        albumId: albumId || null
      };
      const formData = new FormData();
      formData.append('file', this.selectedFile);
      formData.append('metadata', new Blob([JSON.stringify(metadata)], { type: 'application/json' }));
      this.http.post(`${this.API_URL}/tracks/upload`, formData).subscribe({
        next: () => {
          this.isUploading = false;
          this.snackBar.open('Titre uploadé avec succès !', 'OK', { duration: 3000 });
          this.router.navigate(['/catalogue']);
        },
        error: () => {
          this.isUploading = false;
          this.snackBar.open('Erreur lors de l\'upload', 'OK', { duration: 3000 });
        }
      });
    } catch {
      this.isUploading = false;
      this.snackBar.open('Erreur lors de la création', 'OK', { duration: 3000 });
    }
  }

  get canUpload(): boolean {
    if (this.mode === 'spotify') {
      const hasAudio = !!this.selectedSpotifyTrack?.preview_url || !!this.spotifyFallbackFile;
      return !!this.selectedSpotifyTrack && hasAudio && !!this.newArtistName;
    }
    const hasArtist = this.showNewArtistForm ? !!this.newArtistName : !!this.selectedArtistId;
    return !!this.selectedFile && !!this.trackTitle && hasArtist;
  }
}