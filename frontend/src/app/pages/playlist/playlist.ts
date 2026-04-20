import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { FormsModule } from '@angular/forms';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Playlist as PlaylistModel } from '../../models/playlist';
import { PlaylistService } from '../../services/playlist';

@Component({
  selector: 'app-playlist',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatChipsModule,
    MatTooltipModule,
  ],
  templateUrl: './playlist.html',
  styleUrl: './playlist.scss'
})
export class PlaylistPage implements OnInit {
  playlists: PlaylistModel[] = [];
  selectedPlaylist: PlaylistModel | null = null;
  showCreateForm = false;
  newPlaylistName = '';
  newPlaylistType: 'MANUAL' | 'SMART' = 'MANUAL';
  smartMood = '';
  smartGenre = '';

  moods = ['HAPPY', 'SAD', 'CHILL', 'FOCUS', 'HYPE', 'ROMANTIC', 'WORKOUT', 'ANGRY'];
  genres = ['POP', 'ROCK', 'JAZZ', 'CLASSICAL', 'ELECTRONIC', 'HIP_HOP', 'REGGAE', 'METAL', 'RNB'];

  constructor(private playlistService: PlaylistService) {}

  ngOnInit(): void {
    this.playlistService.getAll().subscribe(playlists => {
      this.playlists = playlists;
      if (playlists.length > 0) {
        this.selectedPlaylist = playlists[0];
      }
    });
  }

  selectPlaylist(playlist: PlaylistModel): void {
    this.selectedPlaylist = playlist;
  }

  createPlaylist(): void {
    if (!this.newPlaylistName.trim()) return;

    const dto = {
      name: this.newPlaylistName,
      type: this.newPlaylistType,
      ...(this.newPlaylistType === 'SMART' && this.smartMood && { mood: this.smartMood as any }),
      ...(this.newPlaylistType === 'SMART' && this.smartGenre && { genre: this.smartGenre as any }),
    };

    this.playlistService.create(dto).subscribe(created => {
      this.playlists = [...this.playlists, created];
      this.selectedPlaylist = created;
      this.showCreateForm = false;
      this.newPlaylistName = '';
      this.smartMood = '';
      this.smartGenre = '';
    });
  }

  formatDuration(seconds: number): string {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  deletePlaylist(playlist: PlaylistModel, event: Event): void {
    event.stopPropagation();
    this.playlistService.delete(playlist.id).subscribe(() => {
      this.playlists = this.playlists.filter(p => p.id !== playlist.id);
      if (this.selectedPlaylist?.id === playlist.id) {
        this.selectedPlaylist = this.playlists[0] ?? null;
      }
    });
  }

  getTotalDuration(playlist: PlaylistModel): string {
    const total = playlist.tracks.reduce((acc, t) => acc + (t.duration || 0), 0);
    return this.formatDuration(total);
  }
}
