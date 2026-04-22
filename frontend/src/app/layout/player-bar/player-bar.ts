import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSliderModule } from '@angular/material/slider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { PlayerService, PlayerState } from '../../services/player';
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

  constructor(private playerService: PlayerService) {}

  ngOnInit(): void {
    this.playerService.state$.subscribe(state => {
      this.state = state;
    });
  }

  togglePlay(): void {
    this.playerService.togglePlay();
  }

  next(): void {
    this.playerService.next();
  }

  previous(): void {
    this.playerService.previous();
  }

  get hasNext(): boolean {
    return this.playerService.hasNext();
  }

  get hasPrevious(): boolean {
    return this.playerService.hasPrevious();
  }

  toggleMute(): void {
    this.playerService.toggleMute();
  }

  onSeek(event: any): void {
    this.playerService.seek(event.target.value);
  }

  onVolumeChange(event: any): void {
    this.playerService.setVolume(event.target.value);
  }

  formatTime(seconds: number): string {
    if (!seconds || isNaN(seconds)) return '0:00';
    const m = Math.floor(seconds / 60);
    const s = Math.floor(seconds % 60);
    return `${m}:${s.toString().padStart(2, '0')}`;
  }
}