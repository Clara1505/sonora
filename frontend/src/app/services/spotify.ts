import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface SpotifyTrack {
  id: string;
  name: string;
  duration_ms: number;
  preview_url: string | null;
  artists: { id: string; name: string }[];
  album: {
    id: string;
    name: string;
    release_date: string;
    images: { url: string }[];
  };
}

@Injectable({ providedIn: 'root' })
export class SpotifyService {

  private readonly apiUrl = environment.apiUrl;

  private http = inject(HttpClient);

  searchTracks(query: string): Observable<SpotifyTrack[]> {
    return this.http
      .get<{ tracks: { items: SpotifyTrack[] } }>(`${this.apiUrl}/api/spotify/search?q=${encodeURIComponent(query)}`)
      .pipe(map(response => response.tracks.items));
  }
}