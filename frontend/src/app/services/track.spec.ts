import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TrackService } from './track';
import { TrackDto } from '../models/track';

describe('TrackService', () => {
  let service: TrackService;
  let httpMock: HttpTestingController;

  // On utilise TrackDto (IDs plats) qui correspond au format retourné par le backend
  const mockTrack: TrackDto = {
    id: 1,
    title: 'Get Lucky',
    duration: 248,
    genre: 'POP',
    mood: 'HAPPY',
    filePath: 'get-lucky.mp3',
    albumId: undefined,
    artistId: 1,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(TrackService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAll() should GET /api/tracks and return a list', () => {
    service.getAll().subscribe(tracks => {
      expect(tracks.length).toBe(1);
      expect(tracks[0].title).toBe('Get Lucky');
    });
    httpMock.expectOne(r => r.url.includes('/api/tracks') && r.method === 'GET').flush([mockTrack]);
  });

  it('getById() should GET /api/tracks/:id', () => {
    service.getById(1).subscribe(track => {
      expect(track.id).toBe(1);
    });
    httpMock.expectOne(r => r.url.endsWith('/api/tracks/1')).flush(mockTrack);
  });

  it('create() should POST to /api/tracks', () => {
    service.create({ title: 'New Track' }).subscribe(track => {
      expect(track.id).toBe(1);
    });
    const req = httpMock.expectOne(r => r.url.includes('/api/tracks') && r.method === 'POST');
    expect(req.request.body).toEqual({ title: 'New Track' });
    req.flush(mockTrack);
  });

  it('update() should PUT to /api/tracks/:id', () => {
    service.update(1, { title: 'Updated' }).subscribe(track => {
      expect(track.title).toBe('Get Lucky');
    });
    const req = httpMock.expectOne(r => r.url.endsWith('/api/tracks/1') && r.method === 'PUT');
    expect(req.request.body).toEqual({ title: 'Updated' });
    req.flush(mockTrack);
  });

  it('delete() should send DELETE to /api/tracks/:id', () => {
    service.delete(1).subscribe();
    const req = httpMock.expectOne(r => r.url.endsWith('/api/tracks/1') && r.method === 'DELETE');
    req.flush(null);
    expect(req.request.method).toBe('DELETE');
  });
});