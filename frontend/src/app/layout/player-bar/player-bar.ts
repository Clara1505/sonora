import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSliderModule } from '@angular/material/slider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { PlayerService, PlayerState } from '../../services/player';
import { FavoriteService } from '../../services/favorite';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-player-bar',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatIconModule,
    MatButtonModule,
    MatSliderModule,
    MatTooltipModule,
  ],
  templateUrl: './player-bar.html',
  styleUrl: './player-bar.scss'
})
export class PlayerBar implements OnInit {
  state: PlayerState | null = null;
  favoriteIds = new Set<number>();

  private playerService = inject(PlayerService);
  private favoriteService = inject(FavoriteService);

  ngOnInit(): void {
    this.playerService.state$.subscribe(state => {
      this.state = state;
    });
    // S'abonne au store centralisé — se met à jour quelle que soit la source
    this.favoriteService.favoriteIds$.subscribe(ids => {
      this.favoriteIds = ids;
    });
  }

  isFavorite(): boolean {
    return this.state?.trackId ? this.favoriteIds.has(this.state.trackId) : false;
  }

  toggleFavorite(): void {
    if (!this.state?.trackId) return;
    const trackId = this.state.trackId;
    if (this.isFavorite()) {
      this.favoriteService.remove(trackId).subscribe();
    } else {
      this.favoriteService.add(trackId).subscribe();
    }
  }

  togglePlay(): void { this.playerService.togglePlay(); }
  next(): void { this.playerService.next(); }
  previous(): void { this.playerService.previous(); }
  get hasNext(): boolean { return this.playerService.hasNext(); }
  get hasPrevious(): boolean { return this.playerService.hasPrevious(); }
  toggleMute(): void { this.playerService.toggleMute(); }

  onSeek(event: Event): void { this.playerService.seek(+(event.target as HTMLInputElement).value); }
  onVolumeChange(event: Event): void { this.playerService.setVolume(+(event.target as HTMLInputElement).value); }

  formatTime(seconds: number): string {
    if (!seconds || isNaN(seconds)) return '0:00';
    const m = Math.floor(seconds / 60);
    const s = Math.floor(seconds % 60);
    return `${m}:${s.toString().padStart(2, '0')}`;
  }
}