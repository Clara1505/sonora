import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { PlaylistService } from './playlist';

describe('PlaylistService', () => {
  let service: PlaylistService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()],
    });
    service = TestBed.inject(PlaylistService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});