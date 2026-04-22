import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { FormsModule } from '@angular/forms';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Playlist as PlaylistModel } from '../../models/playlist';
import { Track } from '../../models/track';
import { PlaylistService } from '../../services/playlist';
import { TrackService } from '../../services/track';
import { PlayerService } from '../../services/player';

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
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatChipsModule,
    MatTooltipModule,
    MatSnackBarModule,
  ],
  templateUrl: './playlist.html',
  styleUrl: './playlist.scss'
})
export class PlaylistPage implements OnInit {
  playlists: PlaylistModel[] = [];
  selectedPlaylist: PlaylistModel | null = null;
  showCreateForm = false;
  showAddModal = false;

  newPlaylistName = '';
  newPlaylistType: 'MANUAL' | 'SMART' = 'MANUAL';
  smartMood = '';
  smartGenre = '';

  allTracks: Track[] = [];
  trackSearchQuery = '';
  filterGenre = '';
  filterMood = '';

  moods = ['HAPPY', 'SAD', 'CHILL', 'FOCUS', 'HYPE', 'ROMANTIC', 'WORKOUT', 'ANGRY'];
  genres = ['POP', 'ROCK', 'JAZZ', 'CLASSICAL', 'ELECTRONIC', 'HIP_HOP', 'REGGAE', 'METAL', 'RNB', 'OTHER'];

  constructor(
    private playlistService: PlaylistService,
    private trackService: TrackService,
    private playerService: PlayerService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.playlistService.getAll().subscribe(playlists => {
      this.playlists = playlists;
      if (playlists.length > 0) this.selectedPlaylist = playlists[0];
    });
    this.trackService.getAll().subscribe(tracks => {
      this.allTracks = tracks;
    });
  }

  selectPlaylist(playlist: PlaylistModel): void {
    this.selectedPlaylist = playlist;
    this.showAddModal = false;
  }

  openAddModal(): void {
    this.trackSearchQuery = '';
    this.filterGenre = '';
    this.filterMood = '';
    this.showAddModal = true;
  }

  closeAddModal(): void {
    this.showAddModal = false;
  }

  playTrack(track: any, event: Event): void {
    event.stopPropagation();
    if (!this.selectedPlaylist) return;
    const queue = this.selectedPlaylist.tracks.map(t => ({
      id: t.id,
      title: t.title,
      artist: t.artist?.name || ''
    }));
    const index = queue.findIndex(t => t.id === track.id);
    this.playerService.setQueue(queue, index >= 0 ? index : 0);
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

  addTrack(track: Track): void {
    if (!this.selectedPlaylist) return;
    if (this.isTrackInPlaylist(track)) {
      this.snackBar.open('Déjà dans la playlist', 'OK', { duration: 2000 });
      return;
    }
    this.playlistService.addTrack(this.selectedPlaylist.id, track.id).subscribe(updated => {
      this.selectedPlaylist = updated;
      this.playlists = this.playlists.map(p => p.id === updated.id ? updated : p);
      this.snackBar.open(`"${track.title}" ajouté`, 'OK', { duration: 2000 });
    });
  }

  removeTrack(trackId: number, event: Event): void {
    event.stopPropagation();
    if (!this.selectedPlaylist) return;
    this.playlistService.removeTrack(this.selectedPlaylist.id, trackId).subscribe(updated => {
      this.selectedPlaylist = updated;
      this.playlists = this.playlists.map(p => p.id === updated.id ? updated : p);
    });
  }

  isTrackInPlaylist(track: Track): boolean {
    return this.selectedPlaylist?.tracks.some(t => t.id === track.id) ?? false;
  }

  get filteredTracks(): Track[] {
    return this.allTracks.filter(t => {
      const matchSearch = !this.trackSearchQuery.trim() ||
        t.title.toLowerCase().includes(this.trackSearchQuery.toLowerCase()) ||
        t.artist?.name?.toLowerCase().includes(this.trackSearchQuery.toLowerCase());
      const matchGenre = !this.filterGenre || t.genre === this.filterGenre;
      const matchMood = !this.filterMood || t.mood === this.filterMood;
      return matchSearch && matchGenre && matchMood;
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