import { Component, OnInit } from '@angular/core';
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
import { HttpClient } from '@angular/common/http';
import { ArtistService } from '../../services/artist';
import { AlbumService } from '../../services/album';
import { Genre, Mood } from '../../models/track';

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
  ],
  templateUrl: './upload.html',
  styleUrl: './upload.scss'
})
export class Upload implements OnInit {
  private readonly API_URL = 'http://localhost:8080/api';

  // Fichier audio
  selectedFile: File | null = null;

  // Artiste
  artists: any[] = [];
  selectedArtistId: number | null = null;
  showNewArtistForm = false;
  newArtistName = '';
  newArtistBio = '';

  // Album
  albums: any[] = [];
  filteredAlbums: any[] = [];
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
  uploadProgress = 0;

  constructor(
    private artistService: ArtistService,
    private albumService: AlbumService,
    private http: HttpClient,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadArtists();
    this.loadAlbums();
  }

  loadArtists(): void {
    this.artistService.getAll().subscribe(artists => {
      this.artists = artists;
    });
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

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      if (!this.trackTitle) {
        this.trackTitle = file.name.replace(/\.[^.]+$/, '');
      }
    }
  }

  async createArtistIfNeeded(): Promise<number> {
    if (!this.showNewArtistForm) return this.selectedArtistId!;
    const artist = await this.artistService.create({
      name: this.newArtistName,
      bio: this.newArtistBio
    }).toPromise();
    this.artists = [...this.artists, artist];
    return artist!.id;
  }

  async createAlbumIfNeeded(artistId: number): Promise<number> {
    if (!this.showNewAlbumForm) return this.selectedAlbumId!;
    const album = await this.albumService.create({
      title: this.newAlbumTitle,
      releaseYear: this.newAlbumYear,
      artistId: artistId
    }).toPromise();
    this.albums = [...this.albums, album];
    return (album as any)!.id;
  }

  async upload(): Promise<void> {
    if (!this.selectedFile || !this.trackTitle) {
      this.snackBar.open('Veuillez sélectionner un fichier et un titre', 'OK', { duration: 3000 });
      return;
    }
    if (!this.showNewArtistForm && !this.selectedArtistId) {
      this.snackBar.open('Veuillez sélectionner ou créer un artiste', 'OK', { duration: 3000 });
      return;
    }

    this.isUploading = true;
    this.uploadProgress = 0;

    try {
      const artistId = await this.createArtistIfNeeded();
      const albumId = await this.createAlbumIfNeeded(artistId);

      const metadata = {
        title: this.trackTitle,
        genre: this.selectedGenre || null,
        mood: this.selectedMood || null,
        artistId: artistId,
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
    const hasArtist = this.showNewArtistForm ? !!this.newArtistName : !!this.selectedArtistId;
    const hasAlbum = this.showNewAlbumForm ? !!this.newAlbumTitle : true;
    return !!this.selectedFile && !!this.trackTitle && hasArtist && hasAlbum;
  }
}