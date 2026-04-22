import { environment } from '../../environments/environment';
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AlbumDto {
  id: number;
  title: string;
  releaseYear?: number;
  artistId?: number;
}

export interface AlbumCreateRequest {
  title: string;
  releaseYear: number;
  artistId: number;
}

@Injectable({ providedIn: 'root' })
export class AlbumService {
  private readonly API_URL = `${environment.apiUrl}/api/albums`;
  private http = inject(HttpClient);

  getAll(): Observable<AlbumDto[]> {
    return this.http.get<AlbumDto[]>(this.API_URL);
  }

  getById(id: number): Observable<AlbumDto> {
    return this.http.get<AlbumDto>(`${this.API_URL}/${id}`);
  }

  create(dto: AlbumCreateRequest): Observable<AlbumDto> {
    return this.http.post<AlbumDto>(this.API_URL, dto);
  }
}
