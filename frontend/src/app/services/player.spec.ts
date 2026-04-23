import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { PlayerService, PlayerState } from './player';

describe('PlayerService', () => {
  let service: PlayerService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(PlayerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('state initial', () => {
    it('should start with no track playing', () => {
      let state!: PlayerState;
      service.state$.subscribe(s => (state = s));

      expect(state.isPlaying).toBe(false);
      expect(state.trackId).toBeNull();
      expect(state.volume).toBe(80);
    });
  });

  describe('setVolume()', () => {
    it('should update the volume in state', () => {
      let state!: PlayerState;
      service.state$.subscribe(s => (state = s));

      service.setVolume(50);

      expect(state.volume).toBe(50);
    });

    it('should set isMuted to true when volume is 0', () => {
      let state!: PlayerState;
      service.state$.subscribe(s => (state = s));

      service.setVolume(0);

      expect(state.isMuted).toBe(true);
    });

    it('should set isMuted to false when volume is above 0', () => {
      let state!: PlayerState;
      service.state$.subscribe(s => (state = s));

      service.setVolume(0);
      service.setVolume(60);

      expect(state.isMuted).toBe(false);
    });
  });

  describe('seek()', () => {
    it('should update currentTime in state', () => {
      let state!: PlayerState;
      service.state$.subscribe(s => (state = s));

      service.seek(42);

      expect(state.currentTime).toBe(42);
    });
  });

  describe('queue management', () => {
    const tracks = [
      { id: 1, title: 'Track A', artist: 'Artist 1' },
      { id: 2, title: 'Track B', artist: 'Artist 2' },
      { id: 3, title: 'Track C', artist: 'Artist 3' },
    ];

    it('hasNext() should return false on an empty queue', () => {
      expect(service.hasNext()).toBe(false);
    });

    it('hasPrevious() should return false on an empty queue', () => {
      expect(service.hasPrevious()).toBe(false);
    });

    it('setQueue() should load the queue and report hasNext correctly', () => {
      service.setQueue(tracks, 0);
      // Flush any HTTP requests triggered by play()
      httpMock.match(() => true).forEach(r => r.flush(''));

      expect(service.hasNext()).toBe(true);
    });

    it('setQueue() at last index should report hasNext as false', () => {
      service.setQueue(tracks, 2);
      httpMock.match(() => true).forEach(r => r.flush(''));

      expect(service.hasNext()).toBe(false);
    });

    it('next() should advance the queue index and update trackId in state', () => {
      service.setQueue(tracks, 0);
      httpMock.match(() => true).forEach(r => r.flush(''));

      let state!: PlayerState;
      service.state$.subscribe(s => (state = s));

      service.next();
      httpMock.match(() => true).forEach(r => r.flush(''));

      expect(state.trackId).toBe(2);
      expect(state.trackTitle).toBe('Track B');
    });

    it('next() should be a no-op at the end of the queue', () => {
      service.setQueue(tracks, 2);
      httpMock.match(() => true).forEach(r => r.flush(''));

      let state!: PlayerState;
      service.state$.subscribe(s => (state = s));
      const initialId = state.trackId;

      service.next();

      expect(state.trackId).toBe(initialId);
    });
  });

  describe('state$ observable', () => {
    it('should emit a new state object on each update', () => {
      const emitted: PlayerState[] = [];
      service.state$.subscribe(s => emitted.push({ ...s }));

      service.setVolume(30);
      service.setVolume(70);

      expect(emitted.length).toBeGreaterThanOrEqual(3); // initial + 2 updates
      expect(emitted[emitted.length - 1].volume).toBe(70);
    });
  });
});