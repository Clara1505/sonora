import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Playlist, PlaylistCreateRequest } from '../models/playlist';

@Injectable({ providedIn: 'root' })
export class PlaylistService {
  private readonly API_URL = `${environment.apiUrl}/api/playlists`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Playlist[]> {
    return this.http.get<Playlist[]>(this.API_URL);
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
}
