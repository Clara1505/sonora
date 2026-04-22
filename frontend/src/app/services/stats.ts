import { environment } from '../../environments/environment';
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TrackStatsResponse {
  trackId: number;
  trackTitle: string;
  playCount: number;
  totalDurationListened: number;
}

export interface UserStatsResponse {
  userEmail: string;
  totalPlays: number;
  topTracks: TrackStatsResponse[];
}

@Injectable({ providedIn: 'root' })
export class StatsService {
  private readonly API_URL = `${environment.apiUrl}/api/stats`;

  private http = inject(HttpClient);

  getMyStats(): Observable<UserStatsResponse> {
    return this.http.get<UserStatsResponse>(`${this.API_URL}/me`);
  }

  getTrackStats(trackId: number): Observable<TrackStatsResponse> {
    return this.http.get<TrackStatsResponse>(`${this.API_URL}/tracks/${trackId}`);
  }
}