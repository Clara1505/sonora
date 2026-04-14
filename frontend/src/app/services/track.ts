import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Track } from '../models/track';

@Injectable({ providedIn: 'root' })
export class TrackService {
  private readonly API_URL = 'http://localhost:8080/api/tracks';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Track[]> {
    return this.http.get<Track[]>(this.API_URL);
  }

  getById(id: number): Observable<Track> {
    return this.http.get<Track>(`${this.API_URL}/${id}`);
  }

  create(dto: Partial<Track>): Observable<Track> {
    return this.http.post<Track>(this.API_URL, dto);
  }

  update(id: number, dto: Partial<Track>): Observable<Track> {
    return this.http.put<Track>(`${this.API_URL}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}