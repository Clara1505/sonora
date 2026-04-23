import { Injectable, inject } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

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

  private queue: { id: number; title: string; artist: string }[] = [];
  private queueIndex = 0;

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

  private http = inject(HttpClient);

  constructor() {
    this.audio.volume = 0.8;

    this.audio.ontimeupdate = () => {
      this.updateState({ currentTime: this.audio.currentTime });
    };

    this.audio.ondurationchange = () => {
      this.updateState({ duration: this.audio.duration });
    };

    this.audio.onended = () => {
      this.notifyTrackPlayed();
      if (this.hasNext()) {
        this.next();
      } else {
        this.updateState({ isPlaying: false, currentTime: 0 });
      }
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

    if (this.state.trackId) {
      this.notifyTrackPlayed();
    }

    this.audio.src = url;
    this.audio.load();
    this.audio.play();
    this.updateState({ trackId, trackTitle, artistName, currentTime: 0 });
  }

  stop(): void {
    if (this.state.trackId) {
      this.notifyTrackPlayed();
    }
    this.audio.pause();
    this.audio.src = '';
    this.queue = [];
    this.queueIndex = 0;
    this.updateState({
      isPlaying: false,
      currentTime: 0,
      duration: 0,
      trackId: null,
      trackTitle: '',
      artistName: ''
    });
  }

  setQueue(tracks: { id: number; title: string; artist: string }[], startIndex = 0): void {
    this.queue = tracks;
    this.queueIndex = startIndex;
    const t = tracks[startIndex];
    if (t) this.play(t.id, t.title, t.artist);
  }

  next(): void {
    if (!this.hasNext()) return;
    this.queueIndex++;
    const t = this.queue[this.queueIndex];
    this.play(t.id, t.title, t.artist);
  }

  previous(): void {
    if (this.audio.currentTime > 3) {
      this.seek(0);
      return;
    }
    if (!this.hasPrevious()) return;
    this.queueIndex--;
    const t = this.queue[this.queueIndex];
    this.play(t.id, t.title, t.artist);
  }

  hasNext(): boolean {
    return this.queue.length > 0 && this.queueIndex < this.queue.length - 1;
  }

  hasPrevious(): boolean {
    return this.queue.length > 0 && (this.queueIndex > 0 || this.audio.currentTime > 3);
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
    }).subscribe();
  }

  private updateState(partial: Partial<PlayerState>): void {
    this.state = { ...this.state, ...partial };
    this.stateSubject.next({ ...this.state });
  }
}