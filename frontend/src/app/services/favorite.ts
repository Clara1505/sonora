import { environment } from '../../environments/environment';
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

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
  private readonly API_URL = `${environment.apiUrl}/api/favorites`;

  private favoriteIdsSubject = new BehaviorSubject<Set<number>>(new Set());
  favoriteIds$ = this.favoriteIdsSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadAll();
  }

  private loadAll(): void {
    this.http.get<FavoriteResponse[]>(this.API_URL).subscribe({
      next: (favorites) => {
        this.favoriteIdsSubject.next(new Set(favorites.map(f => f.trackId)));
      },
      error: () => { /* noop */ }
    });
  }

  isFavorite(trackId: number): boolean {
    return this.favoriteIdsSubject.value.has(trackId);
  }

  getAll(): Observable<FavoriteResponse[]> {
    return this.http.get<FavoriteResponse[]>(this.API_URL);
  }

  add(trackId: number): Observable<FavoriteResponse> {
    return this.http.post<FavoriteResponse>(`${this.API_URL}/${trackId}`, {}).pipe(
      tap(() => {
        const ids = new Set(this.favoriteIdsSubject.value);
        ids.add(trackId);
        this.favoriteIdsSubject.next(ids);
      })
    );
  }

  remove(trackId: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${trackId}`).pipe(
      tap(() => {
        const ids = new Set(this.favoriteIdsSubject.value);
        ids.delete(trackId);
        this.favoriteIdsSubject.next(ids);
      })
    );
  }
}