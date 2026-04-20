import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FavoriteResponse {
  id: number;
  trackId: number;
  trackTitle: string;
  artistName: string;
  genre: string;
  mood: string;
  addedAt: string;
}

@Injectable({ providedIn: 'root' })
export class FavoriteService {
  private readonly API_URL = 'http://localhost:8080/api/favorites';

  constructor(private http: HttpClient) {}

  getAll(): Observable<FavoriteResponse[]> {
    return this.http.get<FavoriteResponse[]>(this.API_URL);
  }

  add(trackId: number): Observable<FavoriteResponse> {
    return this.http.post<FavoriteResponse>(`${this.API_URL}/${trackId}`, {});
  }

  remove(trackId: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${trackId}`);
  }
}