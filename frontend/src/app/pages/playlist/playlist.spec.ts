import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlaylistPage } from './playlist';

describe('Playlist', () => {
  let component: PlaylistPage;
  let fixture: ComponentFixture<PlaylistPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlaylistPage],
    }).compileComponents();

    fixture = TestBed.createComponent(PlaylistPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
