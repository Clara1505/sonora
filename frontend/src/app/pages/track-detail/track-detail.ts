import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { Track } from '../../models/track';

// Mock data en attendant le backend
const MOCK_TRACKS: Track[] = [
  {
    id: 1, title: 'Get Lucky', duration: 248, genre: 'POP', mood: 'HAPPY',
    artist: { id: 1, name: 'Daft Punk', bio: 'Duo de musique électronique français formé en 1993.' },
    album: { id: 1, title: 'Random Access Memories', releaseYear: 2013, artist: { id: 1, name: 'Daft Punk' } }
  },
  {
    id: 2, title: 'Bohemian Rhapsody', duration: 355, genre: 'ROCK', mood: 'HYPE',
    artist: { id: 2, name: 'Queen', bio: 'Groupe de rock britannique formé à Londres en 1970.' },
    album: { id: 2, title: 'A Night at the Opera', releaseYear: 1975, artist: { id: 2, name: 'Queen' } }
  },
  {
    id: 3, title: 'So What', duration: 562, genre: 'JAZZ', mood: 'CHILL',
    artist: { id: 3, name: 'Miles Davis', bio: 'Trompettiste et compositeur de jazz américain.' },
    album: { id: 3, title: 'Kind of Blue', releaseYear: 1959, artist: { id: 3, name: 'Miles Davis' } }
  },
];

@Component({
  selector: 'app-track-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatDividerModule,
  ],
  templateUrl: './track-detail.html',
  styleUrl: './track-detail.scss'
})
export class TrackDetail implements OnInit {
  track: Track | null = null;
  isLoading = true;
  isFavorite = false;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    setTimeout(() => {
      this.track = MOCK_TRACKS.find(t => t.id === id) || null;
      this.isLoading = false;
    }, 300);
  }

  formatDuration(seconds: number): string {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  toggleFavorite(): void {
    this.isFavorite = !this.isFavorite;
  }
}