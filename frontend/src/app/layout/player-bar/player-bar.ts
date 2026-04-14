import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSliderModule } from '@angular/material/slider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Track } from '../../models/track';

@Component({
  selector: 'app-player-bar',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule,
    MatSliderModule,
    MatTooltipModule,
  ],
  templateUrl: './player-bar.html',
  styleUrl: './player-bar.scss'
})
export class PlayerBar {
  // Mock track pour le test visuel
  currentTrack: Track | null = null;

  isPlaying = false;
  currentTime = 0;
  volume = 80;
  isMuted = false;

  togglePlay(): void {
    this.isPlaying = !this.isPlaying;
  }

  toggleMute(): void {
    this.isMuted = !this.isMuted;
  }

  formatTime(seconds: number): string {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  get progressPercent(): number {
    if (!this.currentTrack?.duration) return 0;
    return (this.currentTime / this.currentTrack.duration) * 100;
  }
}