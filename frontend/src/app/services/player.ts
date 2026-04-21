import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export interface PlayerState {
  isPlaying: boolean;
  currentTime: number;
  duration: number;
  volume: number;
  isMuted: boolean;
  trackId: number | null;
  trackTitle: string;
  artistName: string;
}

@Injectable({ providedIn: 'root' })
export class PlayerService {
  private audio = new Audio();
  private readonly API_URL = `${environment.apiUrl}/api/tracks`;
  private playStartTime = 0;

  private state: PlayerState = {
    isPlaying: false,
    currentTime: 0,
    duration: 0,
    volume: 80,
    isMuted: false,
    trackId: null,
    trackTitle: '',
    artistName: '',
  };

  private stateSubject = new BehaviorSubject<PlayerState>(this.state);
  state$ = this.stateSubject.asObservable();

  constructor(private http: HttpClient) {
    this.audio.volume = 0.8;

    this.audio.ontimeupdate = () => {
      this.updateState({ currentTime: this.audio.currentTime });
    };

    this.audio.ondurationchange = () => {
      this.updateState({ duration: this.audio.duration });
    };

    this.audio.onended = () => {
      this.notifyTrackPlayed();
      this.updateState({ isPlaying: false, currentTime: 0 });
    };

    this.audio.onplay = () => {
      this.playStartTime = Date.now();
      this.updateState({ isPlaying: true });
    };

    this.audio.onpause = () => {
      this.updateState({ isPlaying: false });
    };
  }

  play(trackId: number, trackTitle: string, artistName: string): void {
    const url = `${this.API_URL}/${trackId}/stream`;

    if (this.state.trackId === trackId) {
      this.togglePlay();
      return;
    }

    // Notifie l'écoute de la track précédente avant de changer
    if (this.state.trackId && !this.audio.paused) {
      this.notifyTrackPlayed();
    }

    this.audio.src = url;
    this.audio.load();
    this.audio.play();
    this.updateState({ trackId, trackTitle, artistName, currentTime: 0 });
  }

  togglePlay(): void {
    if (this.audio.paused) {
      this.audio.play();
    } else {
      this.audio.pause();
    }
  }

  seek(time: number): void {
    this.audio.currentTime = time;
    this.updateState({ currentTime: time });
  }

  setVolume(volume: number): void {
    this.audio.volume = volume / 100;
    this.updateState({ volume, isMuted: volume === 0 });
  }

  toggleMute(): void {
    this.audio.muted = !this.audio.muted;
    this.updateState({ isMuted: this.audio.muted });
  }

  private notifyTrackPlayed(): void {
    if (!this.state.trackId) return;
    const durationListened = Math.floor(this.audio.currentTime) || 1;
    this.http.post(`${this.API_URL}/${this.state.trackId}/played`, {
      durationListened
    }).subscribe({ error: () => {} });
  }

  private updateState(partial: Partial<PlayerState>): void {
    this.state = { ...this.state, ...partial };
    this.stateSubject.next({ ...this.state });
  }
}