import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AlbumService {
  private readonly API_URL = 'http://localhost:8080/api/albums';

  constructor(private http: HttpClient) {}

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.API_URL);
  }

  getById(id: number): Observable<any> {
    return this.http.get<any>(`${this.API_URL}/${id}`);
  }

  create(dto: any): Observable<any> {
    return this.http.post<any>(this.API_URL, dto);
  }
}