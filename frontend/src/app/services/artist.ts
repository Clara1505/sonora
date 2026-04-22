import { environment } from '../../environments/environment';
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Artist } from '../models/artist';

@Injectable({ providedIn: 'root' })
export class ArtistService {
  private readonly API_URL = `${environment.apiUrl}/api/artists`;
  private http = inject(HttpClient);

  getAll(): Observable<Artist[]> {
    return this.http.get<Artist[]>(this.API_URL);
  }

  getById(id: number): Observable<Artist> {
    return this.http.get<Artist>(`${this.API_URL}/${id}`);
  }

  create(dto: Partial<Artist>): Observable<Artist> {
    return this.http.post<Artist>(this.API_URL, dto);
  }

  update(id: number, dto: Partial<Artist>): Observable<Artist> {
    return this.http.put<Artist>(`${this.API_URL}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}