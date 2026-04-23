import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Playlist, PlaylistCreateRequest } from '../models/playlist';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PlaylistService {
  private readonly API_URL = `${environment.apiUrl}/api/playlists`;

  private http = inject(HttpClient);

  getAll(): Observable<Playlist[]> {
    return this.http.get<Playlist[]>(this.API_URL);
  }

  getAllAdmin(): Observable<Playlist[]> {
    return this.http.get<Playlist[]>(`${this.API_URL}/all`);
  }

  getById(id: number): Observable<Playlist> {
    return this.http.get<Playlist>(`${this.API_URL}/${id}`);
  }

  create(dto: PlaylistCreateRequest): Observable<Playlist> {
    return this.http.post<Playlist>(this.API_URL, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  addTrack(playlistId: number, trackId: number): Observable<Playlist> {
    return this.http.post<Playlist>(`${this.API_URL}/${playlistId}/tracks/${trackId}`, {});
  }

  removeTrack(playlistId: number, trackId: number): Observable<Playlist> {
    return this.http.delete<Playlist>(`${this.API_URL}/${playlistId}/tracks/${trackId}`);
  }
}