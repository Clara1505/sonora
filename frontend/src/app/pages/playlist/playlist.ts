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

const MOCK_PLAYLISTS: PlaylistModel[] = [
  {
    id: 1,
    name: 'Mes favoris',
    type: 'MANUAL',
    createdAt: '2024-01-15',
    tracks: [
      {
        id: 1, title: 'Get Lucky', duration: 248, genre: 'POP', mood: 'HAPPY',
        artist: { id: 1, name: 'Daft Punk' }
      },
      {
        id: 2, title: 'Bohemian Rhapsody', duration: 355, genre: 'ROCK', mood: 'HYPE',
        artist: { id: 2, name: 'Queen' }
      },
    ]
  },
  {
    id: 2,
    name: 'Focus & Jazz',
    type: 'SMART',
    createdAt: '2024-02-20',
    tracks: [
      {
        id: 3, title: 'So What', duration: 562, genre: 'JAZZ', mood: 'CHILL',
        artist: { id: 3, name: 'Miles Davis' }
      },
      {
        id: 6, title: 'Clair de Lune', duration: 318, genre: 'CLASSICAL', mood: 'CHILL',
        artist: { id: 6, name: 'Debussy' }
      },
    ]
  },
];

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

  ngOnInit(): void {
    this.playlists = MOCK_PLAYLISTS;
    this.selectedPlaylist = this.playlists[0];
  }

  selectPlaylist(playlist: PlaylistModel): void {
    this.selectedPlaylist = playlist;
  }

  createPlaylist(): void {
    if (!this.newPlaylistName.trim()) return;
    const newPlaylist: PlaylistModel = {
      id: Date.now(),
      name: this.newPlaylistName,
      type: this.newPlaylistType,
      createdAt: new Date().toISOString(),
      tracks: []
    };
    this.playlists = [...this.playlists, newPlaylist];
    this.selectedPlaylist = newPlaylist;
    this.showCreateForm = false;
    this.newPlaylistName = '';
  }

  formatDuration(seconds: number): string {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  getTotalDuration(playlist: PlaylistModel): string {
    const total = playlist.tracks.reduce((acc, t) => acc + (t.duration || 0), 0);
    return this.formatDuration(total);
  }
}