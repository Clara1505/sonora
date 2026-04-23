import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TrackService } from '../../services/track';
import { TrackDto } from '../../models/track';
import { ArtistService } from '../../services/artist';
import { FavoriteService } from '../../services/favorite';
import { PlayerService } from '../../services/player';
import { forkJoin, Observable } from 'rxjs';

@Component({
  selector: 'app-track-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatDividerModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './track-detail.html',
  styleUrl: './track-detail.scss'
})
export class TrackDetail implements OnInit {
  track: TrackDto | null = null;
  artistName = '';
  artistBio = '';
  isLoading = true;
  isFavorite = false;
  private route = inject(ActivatedRoute);
  private trackService = inject(TrackService);
  private artistService = inject(ArtistService);
  private favoriteService = inject(FavoriteService);
  private playerService = inject(PlayerService);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    forkJoin({
      track: this.trackService.getById(id) as unknown as Observable<TrackDto>,
      favorites: this.favoriteService.getAll()
    }).subscribe({
      next: ({ track, favorites }) => {
        this.track = track;
        this.isFavorite = favorites.some(f => f.trackId === track.id);
        if (track.artistId) {
          this.artistService.getById(track.artistId).subscribe(artist => {
            this.artistName = artist.name;
            this.artistBio = artist.bio ?? '';
          });
        }
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  formatDuration(seconds: number): string {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  playTrack(): void {
    if (this.track) {
      this.playerService.play(this.track.id, this.track.title, this.artistName);
    }
  }

  toggleFavorite(): void {
    if (!this.track) return;
    if (this.isFavorite) {
      this.favoriteService.remove(this.track.id).subscribe(() => {
        this.isFavorite = false;
      });
    } else {
      this.favoriteService.add(this.track.id).subscribe(() => {
        this.isFavorite = true;
      });
    }
  }
}